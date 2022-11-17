package com.vpbanks.timeline.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@Slf4j
public class ThreadPoolConfig {

	@Bean("threadTaskSchedule")
	public ThreadPoolTaskExecutor threadTaskSchedule() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(10);
		executor.setThreadNamePrefix("taskScheduleThread-");
		executor.initialize();
		return executor;
	}

	@Bean("threadJobExecuted")
	public ThreadPoolTaskExecutor threadJobExecuted() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(10);
		executor.setThreadNamePrefix("threadJobExecuted-");
		executor.initialize();
		return executor;
	}

	@Bean(name = "taskExecutor")
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(8);
		executor.setThreadNamePrefix("timelineService-");
		executor.initialize();
		return executor;
	}

}
