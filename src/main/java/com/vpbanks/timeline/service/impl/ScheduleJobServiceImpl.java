package com.vpbanks.timeline.service.impl;

import com.vpbanks.timeline.config.redis.RedisCache;
import com.vpbanks.timeline.constants.BaseConfigConstant;
import com.vpbanks.timeline.constants.ErrorConstant;
import com.vpbanks.timeline.repository.EventManagementRepository;
import com.vpbanks.timeline.repository.JobMonitorRepository;
import com.vpbanks.timeline.repository.entity.EventManagementEntity;
import com.vpbanks.timeline.repository.entity.JobMonitorEntity;
import com.vpbanks.timeline.request.EvenTriggerBondRequestDto;
import com.vpbanks.timeline.request.EvenTriggerCopyTradeRequestDto;
import com.vpbanks.timeline.request.JobExecutedRequestDto;
import com.vpbanks.timeline.request.KafkaRequestDto;
import com.vpbanks.timeline.response.ResponseDto;
import com.vpbanks.timeline.response.exception.ServiceException;
import com.vpbanks.timeline.service.BaseService;
import com.vpbanks.timeline.service.EventManagementService;
import com.vpbanks.timeline.service.ScheduleJobService;
import com.vpbanks.timeline.service.kafka.KafkaProduceService;
import com.vpbanks.timeline.util.JsonUtil;
import com.vpbanks.timeline.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleJobServiceImpl extends BaseService implements ScheduleJobService{

    private final RedisCache redisCache;
    private final EventManagementService eventManagementService;
    private final EventManagementRepository eventManagementRepository;
    private final JobMonitorRepository jobMonitorRepository;
    private final KafkaProduceService kafkaProduceService;
    private final ResponseUtils responseUtils;
    @Qualifier("threadTaskSchedule")
    private final ThreadPoolTaskExecutor threadTaskSchedule;
    @Value("${spring.kafka.producer.timeline.trigger.event.bond}")
    private String topicEventBond;
    @Value("${spring.kafka.producer.timeline.trigger.event.copytrade}")
    private String topicEventCopyTrade;

    @Override
    public void scanEvent(JobExecutedRequestDto request, String id) throws Exception {
        List<String> statuses = new ArrayList<>();
        JobMonitorEntity jobMonitorEntity = null;
        if (Objects.isNull(request) && StringUtils.hasLength(id)) {
            jobMonitorEntity = jobMonitorRepository.findById(id).orElseThrow(
                    () -> new ServiceException(ErrorConstant.TimeLineErrorCode.DATA_NOT_FOUND));
            request = new JobExecutedRequestDto();
            request.setTimeStart(jobMonitorEntity.getTimeStart());
            request.setTimeEnd(jobMonitorEntity.getTimeEnd());
            statuses = Arrays.asList(BaseConfigConstant.StatusEnum.WAITING.getValue(), BaseConfigConstant.StatusEnum.FAILED_BY_SENDING.getValue(), BaseConfigConstant.StatusEnum.FAILED_BY_EXECUTING.getValue());
        } else {
            statuses.add(BaseConfigConstant.StatusEnum.WAITING.getValue());
        }
        log.info("========START======== {}", JsonUtil.toJsonString(request));
        long begin = new Date().getTime();
        long count = eventManagementRepository.countByScantEvent(request.getTimeStart(), request.getTimeEnd(),
                statuses, BaseConfigConstant.ACTIVE);

        log.info("count = {}", count);

        if (BaseConfigConstant.isStopJob){
            log.info("scanEvent stop. Job monitor");
            return;
        }

        //save job monitor
        long p = getPage(count, BaseConfigConstant.PAGE_SIZE_EXECUTED);
        jobMonitorEntity = insertJobMonitor(count, request, id, jobMonitorEntity);
        List<CompletableFuture<Void>> lst = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < p; i++) {
                Integer pageIndex = i;
                JobExecutedRequestDto finalRequest = request;
                JobMonitorEntity finalJobMonitorEntity = jobMonitorEntity;
                List<String> finalStatuses = statuses;

                doExecuted(pageIndex, finalRequest, finalJobMonitorEntity.getId(), finalStatuses);
//                lst.add(CompletableFuture.runAsync(() -> {
//                    try {
//                        doExecuted(pageIndex, finalRequest, finalJobMonitorEntity.getId(), finalStatuses);
//                    } catch (Exception e){
//                        log.info("{} - {} - {}", pageIndex, e, e.getMessage());
//                    }
//                }, threadTaskSchedule));
            }
//            for(CompletableFuture<Void> c : lst){
//                c.get();
//            }

            jobMonitorEntity.setStatus(BaseConfigConstant.StatusEnum.SUCCESS.getValue());
            jobMonitorEntity.setTimeExecute(new Date().getTime()-begin);
//            jobMonitorEntity.setTotalEventSuccess(count);
            jobMonitorRepository.save(jobMonitorEntity);
            BaseConfigConstant.jobMapMonitor.remove(jobMonitorEntity.getId());
        }
        System.out.println("========END========");
    }

    private JobMonitorEntity insertJobMonitor(Long count, JobExecutedRequestDto request, String id, JobMonitorEntity jobMonitorEntity) throws ServiceException {
        if (Objects.isNull(jobMonitorEntity)) {
            jobMonitorEntity = JobMonitorEntity.builder()
                    .totalEvent(count)
                    .timeStart(request.getTimeStart())
                    .timeEnd(request.getTimeEnd())
                    .status(BaseConfigConstant.StatusEnum.PROCESS.getValue())
                    .build();
        } else {
            jobMonitorEntity.setStatus(BaseConfigConstant.StatusEnum.PROCESS.getValue());
        }

        jobMonitorRepository.save(jobMonitorEntity);
        BaseConfigConstant.jobMapMonitor.put(jobMonitorEntity.getId(), BaseConfigConstant.StatusEnum.PROCESS.getValue());
        return jobMonitorEntity;
    }

    private boolean isJobMaintain(String jobMonitorId){
        if (BaseConfigConstant.isStopJob || BaseConfigConstant.jobMapMonitor.isEmpty()
            || BaseConfigConstant.jobMapMonitor.get(jobMonitorId) == null) {
            return true;
        }
        return false;
    }

    private void doExecuted(Integer pageIndex, JobExecutedRequestDto request, String jobMonitorId, List<String> statuses) {
        log.info("pageIndex= {}", pageIndex);
        if(this.isJobMaintain(jobMonitorId)){
            log.info("scanEvent stop. Job monitor {}", jobMonitorId);
            return;
        }

        Pageable paging = PageRequest.of(pageIndex, BaseConfigConstant.PAGE_SIZE_EXECUTED);

        List<EventManagementEntity> eventManagementEntities = eventManagementRepository
                .findByScantEvent(request.getTimeStart(), request.getTimeEnd(),
                        statuses, BaseConfigConstant.ACTIVE, paging)
                .getContent();

        for (EventManagementEntity event : eventManagementEntities) {
            if(this.isJobMaintain(jobMonitorId)){
                log.info("scanEvent stop. Job monitor {}", jobMonitorId);
                return;
            }
            if(event.getTimeEnd() == null || event.getTimeEnd() >= new Date().getTime()){
//                event.setStatus(BaseConfigConstant.StatusEnum.PROCESS.getValue());

                KafkaRequestDto<Object> requestDto = new KafkaRequestDto<>();
                requestDto.setRequestId(request.getRequestId()!=null ? request.getRequestId() : UUID.randomUUID().toString());

                if(BaseConfigConstant.ChannelCodeEnum.BOND_SERVICE.getValue().equals(event.getChannelCode())){
                    EvenTriggerBondRequestDto evenTriggerBondRequestDto = new EvenTriggerBondRequestDto(event);
                    requestDto.setPayload(evenTriggerBondRequestDto);

                    kafkaProduceService.sendEventKafka(requestDto, topicEventBond, jobMonitorId);
                } else if (BaseConfigConstant.ChannelCodeEnum.COPYTRADE_SERVICE.getValue().equals(event.getChannelCode())) {
                    EvenTriggerCopyTradeRequestDto evenTriggerCopyTradeRequestDto = new EvenTriggerCopyTradeRequestDto(event);
                    requestDto.setPayload(evenTriggerCopyTradeRequestDto);

                    kafkaProduceService.sendEventKafka(requestDto, topicEventCopyTrade, jobMonitorId);
                }

                eventManagementService.handleEventRepeat(event);
            }else{
                event.setStatus(BaseConfigConstant.StatusEnum.EXPIRED.getValue());
                eventManagementService.saveEventManagementEntity(event);
            }
        }

//        eventManagementService.saveListEventManagementEntity(eventManagementEntities);
    }

	@Override
	public ResponseDto stopAllJob(boolean isStop, String requestId) {
        ResponseDto res = new ResponseDto(requestId);
        BaseConfigConstant.isStopJob = isStop;
		if (isStop) {
			for (String key : BaseConfigConstant.jobMapMonitor.keySet()) {
				JobMonitorEntity job = jobMonitorRepository.findById(key).get();
				job.setStatus(BaseConfigConstant.StatusEnum.CANCEL.getValue());
				jobMonitorRepository.save(job);
			}
			BaseConfigConstant.jobMapMonitor.clear();
		}
        return responseUtils.setResponseSuccess(res, BaseConfigConstant.isStopJob);
	}

	@Override
	public ResponseDto stopMonitorJob(String monitorId, String requestId) {
        ResponseDto res = new ResponseDto(requestId);
		if (BaseConfigConstant.jobMapMonitor.isEmpty() || Objects.isNull(BaseConfigConstant.jobMapMonitor.get(monitorId))) {
			return responseUtils.setResponseError(res, ErrorConstant.TimeLineErrorCode.DATA_NOT_FOUND.getCode(),
                    ErrorConstant.TimeLineErrorCode.DATA_NOT_FOUND.getMessage());
		}
		JobMonitorEntity job = jobMonitorRepository.findById(monitorId).get();
		if (Objects.isNull(job)) {
            return responseUtils.setResponseError(res, ErrorConstant.TimeLineErrorCode.DATA_NOT_FOUND.getCode(),
                    ErrorConstant.TimeLineErrorCode.DATA_NOT_FOUND.getMessage());
		}
		BaseConfigConstant.jobMapMonitor.remove(monitorId);
		job.setStatus(BaseConfigConstant.StatusEnum.CANCEL.getValue());
		jobMonitorRepository.save(job);

		return responseUtils.setResponseSuccess(res, null);
	}
}
