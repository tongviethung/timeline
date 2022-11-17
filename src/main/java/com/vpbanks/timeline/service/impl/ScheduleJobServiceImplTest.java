package com.vpbanks.timeline.service.impl;

import com.vpbanks.timeline.config.redis.RedisCache;
import com.vpbanks.timeline.constants.BaseConfigConstant;
import com.vpbanks.timeline.repository.EventManagementRepository;
import com.vpbanks.timeline.repository.JobMonitorRepository;
import com.vpbanks.timeline.repository.entity.EventManagementEntity;
import com.vpbanks.timeline.repository.entity.JobMonitorEntity;
import com.vpbanks.timeline.request.JobExecutedRequestDto;
import com.vpbanks.timeline.service.BaseService;
import com.vpbanks.timeline.service.ScheduleJobServiceTest;
import com.vpbanks.timeline.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@AllArgsConstructor
public class ScheduleJobServiceImplTest extends BaseService implements ScheduleJobServiceTest {

    private final RedisCache redisCache;

    private final MongoTemplate mongoTemplate;

    private final EventManagementRepository eventManagementRepository;

    private final JobMonitorRepository jobMonitorRepository;

    @Qualifier("kafkaProducerScanEvent")
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Qualifier("threadTaskSchedule")
    private final ThreadPoolTaskExecutor threadTaskSchedule;

    static Integer PAGE_SIZE = 1000;

    @Override
    public void testScan(JobExecutedRequestDto request, ThreadPoolTaskExecutor taskExecutor) throws Exception {
        log.info("========START======== {}", JsonUtil.toJsonString(request));

        Query query = new Query(
//                Criteria.where("_id").gte(request.getTimeBegin()).andOperator(Criteria.where("_id").lte(request.getTimeEnd()))
        );
        long count = mongoTemplate.count(query, EventManagementEntity.class);

        Date d = new Date();
        System.out.println("DATE= " + d);

        //save job monitor
        JobMonitorEntity jobMonitorEntity = JobMonitorEntity.builder()
                .totalEvent(count)
                .timeStart(new Date().getTime())
                .status(BaseConfigConstant.StatusEnum.PROCESS.getValue())
                .build();
        jobMonitorRepository.save(jobMonitorEntity);

        long p = getPage(count, PAGE_SIZE);
        List<CompletableFuture<Void>> lst = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < p; i++) {
                Integer pageIndex = i;
                lst.add(CompletableFuture.runAsync(() -> {
                    try {
                        doExecutedTest(pageIndex, request);
                    } catch (Exception e) {
                        log.info("testScan: {} - {} - {}", pageIndex, e, e.getMessage());
                    }
                }, taskExecutor));
            }
            for(CompletableFuture<Void> c : lst){
                c.get();
            }

            jobMonitorEntity.setStatus(BaseConfigConstant.StatusEnum.SUCCESS.getValue());
            jobMonitorEntity.setTimeEnd(new Date().getTime());
            jobMonitorRepository.save(jobMonitorEntity);
        }
        System.out.println("========END========");
    }


    public void doExecutedTest(Integer pageIndex, JobExecutedRequestDto request) {
        log.info("pageIndex= {}", pageIndex);
        Pageable paging = PageRequest.of(pageIndex, PAGE_SIZE);

        Query query = new Query(
//                Criteria.where("_id").gte(request.getTimeBegin()).andOperator(Criteria.where("_id").lte(request.getTimeEnd()))
        ).with(paging);
        List<EventManagementEntity> eventManagementEntities = mongoTemplate.find(query, EventManagementEntity.class);

        for (EventManagementEntity e : eventManagementEntities) {
            e.setStatus(BaseConfigConstant.StatusEnum.PROCESS.getValue());
            kafkaTemplate.send("quickstart-events", String.valueOf(e.getId()));
        }
        eventManagementRepository.saveAll(eventManagementEntities);
    }

}
