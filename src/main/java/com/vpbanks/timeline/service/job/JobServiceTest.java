package com.vpbanks.timeline.service.job;

import com.vpbanks.timeline.request.JobExecutedRequestDto;
import com.vpbanks.timeline.service.ScheduleJobServiceTest;
import com.vpbanks.timeline.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
@Slf4j
public class JobServiceTest {

    private final ScheduleJobServiceTest scheduleJobServiceTest;

    static int k = 0;

    @Qualifier("threadJobExecuted")
    private final ThreadPoolTaskExecutor threadJobExecuted;

//    @Scheduled(cron = "0 * * * * ?")
    public void jobExecuted(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(8);
        taskExecutor.setMaxPoolSize(8);
        taskExecutor.afterPropertiesSet();

        JobExecutedRequestDto request = new JobExecutedRequestDto();
        k = k+1;
        if(k==1){
//            request.setTimeBegin(0l);
//            request.setTimeEnd(200000l);
        }else if(k==2){
//            request.setTimeBegin(200000l);
//            request.setTimeEnd(400000l);
        }else{
            System.out.println("==========================STOP==========================");
            return;
        }
        log.info("k= {}", k);
        log.info("request= {}", JsonUtil.toJsonString(request));

        CompletableFuture.runAsync(() -> {
            try {
                scheduleJobServiceTest.testScan(request, taskExecutor);
            } catch (Exception e) {
                log.info("jobExecuted: - {} - {}", e, e.getMessage());
            }
        }, threadJobExecuted);

        System.out.println("hung");
    }
}
