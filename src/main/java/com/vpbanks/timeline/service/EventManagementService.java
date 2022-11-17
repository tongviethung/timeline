package com.vpbanks.timeline.service;

import com.vpbanks.timeline.constants.ErrorConstant.TimeLineErrorCode;
import com.vpbanks.timeline.repository.entity.EventManagementEntity;
import com.vpbanks.timeline.request.BaseEvent;
import com.vpbanks.timeline.request.BaseEventDto;
import com.vpbanks.timeline.request.EventManagementRequestDto;
import com.vpbanks.timeline.request.FindEventRequestDto;
import com.vpbanks.timeline.request.EventResultRequestDto;
import com.vpbanks.timeline.response.ResponseDto;
import com.vpbanks.timeline.response.exception.ServiceException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventManagementService {
    void consumerEventResult(EventResultRequestDto request);

    EventManagementEntity saveEventManagementEntity(EventManagementEntity entity);

    void saveListEventManagementEntity(List<EventManagementEntity> entity);

    ResponseDto findByParam(ResponseDto response, FindEventRequestDto request, Pageable pageable) throws ServiceException;

    ResponseDto cancelEvent(EventManagementRequestDto listEvent, String req) throws ServiceException;

    ResponseDto executedEvents(EventManagementRequestDto request, String requestId) throws ServiceException;

    ResponseDto getJobMonitor(FindEventRequestDto findEventRequestDto) throws ServiceException;

    void handleEventRepeat(EventManagementEntity eventManagementEntity);

    void handleEventError(BaseEventDto baseEventDto, TimeLineErrorCode timeLineErrorCode);

    ResponseDto handleBaseEvent(List<BaseEvent> events, String packageId, String requestId, String channelCode) throws ServiceException;

    ResponseDto getEventType(String requestId);

    ResponseDto getChannelCode(String requestId);
}
