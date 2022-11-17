package com.vpbanks.timeline.service;

import com.vpbanks.timeline.request.JobExecutedRequestDto;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public interface ScheduleJobServiceTest {
	void testScan(JobExecutedRequestDto request, ThreadPoolTaskExecutor taskExecutor) throws Exception;

}
