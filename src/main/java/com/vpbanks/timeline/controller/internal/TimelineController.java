package com.vpbanks.timeline.controller.internal;

import com.vpbanks.timeline.annotation.LogsActivityAnnotation;
import com.vpbanks.timeline.constants.UrlConstant;
import com.vpbanks.timeline.repository.HungTestRepository;
import com.vpbanks.timeline.repository.entity.HungTestEntity;
import com.vpbanks.timeline.request.JobExecutedRequestDto;
import com.vpbanks.timeline.request.StopAllJobRequestDto;
import com.vpbanks.timeline.response.ResponseDto;
import com.vpbanks.timeline.service.BaseService;
import com.vpbanks.timeline.service.ScheduleJobService;
import com.vpbanks.timeline.util.JsonUtil;
import com.vpbanks.timeline.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(UrlConstant.V1_BASIC_ADMIN_URL)
@RequiredArgsConstructor
@Slf4j
public class TimelineController {

    private final ScheduleJobService scheduleJobService;
    @Qualifier("threadJobExecuted")
    private final ThreadPoolTaskExecutor threadJobExecuted;
    private final BaseService baseService;
    private final ResponseUtils responseUtils;

    @PostMapping(UrlConstant.SCHEDULE_EXECUTED_URL)
    @LogsActivityAnnotation
    public void jobExecuted(@RequestBody JobExecutedRequestDto request) {
        log.info("jobExecuted request= {}", JsonUtil.toJsonString(request));

        CompletableFuture.runAsync(() -> {
            try {
                scheduleJobService.scanEvent(request, null);
            } catch (Exception e) {
                log.info("jobExecuted: - {} - {}", e, e.getMessage());
            }
        }, threadJobExecuted);
    }
    
    @PostMapping(UrlConstant.STOP_ALL_JOB)
    @LogsActivityAnnotation
    public ResponseDto stopAllJob(@RequestBody StopAllJobRequestDto request) {
    	log.info("[stopAllJob] requestId: {}, isStop: {}", request.getRequestId(), true);
        ResponseDto responseDto = scheduleJobService.stopAllJob(true, request.getRequestId());
        return responseDto;
    }

    @PostMapping(UrlConstant.STOP_JOB_MONITOR)
    @LogsActivityAnnotation
    public ResponseDto stopJobMonitor(@RequestBody StopAllJobRequestDto request) {
    	return scheduleJobService.stopMonitorJob(request.getMonitorId(), request.getRequestId());
    }


    @PostMapping(UrlConstant.RESTART_JOB)
    @LogsActivityAnnotation
    public void restartJob(@RequestBody StopAllJobRequestDto request) throws Exception {
        log.info("[restartJob] restart job: {}", JsonUtil.toJsonString(request));
        scheduleJobService.scanEvent(null, request.getMonitorId());
    }

    @PostMapping(UrlConstant.UPDATE_CACHE_URL)
    public String updateCache() {
        baseService.initRedisCache();
        return "DONE!!!";
    }

    @PostMapping("/get/cache")
    public ResponseDto getCache() {
        Map<Object, Object> map = baseService.getCache();
        return responseUtils.setResponseSuccess(new ResponseDto(), map);
    }

    @GetMapping("/check")
    public String check() {
        List<CompletableFuture<Void>> lst = new ArrayList<>();
        for(int i=0; i<10000; i++){
            lst.add(CompletableFuture.runAsync(() -> {
                try {
                    doExecuted();
                } catch (Exception e){
                    log.info("{} - {}", e, e.getMessage());
                }
            }, threadJobExecuted));
        }
        return "LIVE";
    }

    private final HungTestRepository hungTestRepository;

    private void doExecuted() {
        HungTestEntity entity = hungTestRepository.findById("6375eafed5b6640ace1efa4a").get();
        entity.setValue(entity.getValue()+1);

        hungTestRepository.save(entity);
    }
}
