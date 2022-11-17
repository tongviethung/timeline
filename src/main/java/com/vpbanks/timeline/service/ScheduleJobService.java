package com.vpbanks.timeline.service;

import java.util.Map;

import com.vpbanks.timeline.request.JobExecutedRequestDto;
import com.vpbanks.timeline.response.ResponseDto;

public interface ScheduleJobService {
	void scanEvent(JobExecutedRequestDto request, String id) throws Exception;

	ResponseDto stopAllJob(boolean isStop, String requestId);

	ResponseDto stopMonitorJob(String monitorId, String requestId);
}
