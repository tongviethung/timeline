package com.vpbanks.timeline.controller;

import com.vpbanks.timeline.annotation.LogsActivityAnnotation;
import com.vpbanks.timeline.annotation.ValidateAnnotation;
import com.vpbanks.timeline.constants.ErrorConstant;
import com.vpbanks.timeline.constants.UrlConstant;
import com.vpbanks.timeline.request.BaseEvent;
import com.vpbanks.timeline.request.BaseEventDto;
import com.vpbanks.timeline.request.EventManagementRequestDto;
import com.vpbanks.timeline.response.ResponseDto;
import com.vpbanks.timeline.response.exception.ServiceException;
import com.vpbanks.timeline.service.EventManagementService;
import com.vpbanks.timeline.util.JsonUtil;
import com.vpbanks.timeline.util.ResponseUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(UrlConstant.V1_BASIC_URL + UrlConstant.EVENT_MANAGEMENT_URL)
public class EventController {

	private final EventManagementService eventManagementService;
	private final ResponseUtils responseUtils;

	@PostMapping(value = UrlConstant.SAVE_EVENTS, consumes = {MediaType.APPLICATION_JSON_VALUE})
	@LogsActivityAnnotation
	public ResponseEntity<Object> saveEvent(@RequestBody BaseEventDto requestDto) {
		String requestId = requestDto.getRequestId();
		log.info("[saveEvent] requestId: {}, request: {}", requestId, JsonUtil.toJsonString(requestDto));
		ResponseDto response = new ResponseDto(requestId);
		try {
			List<BaseEvent> request = requestDto.getEvents();
			response = eventManagementService.handleBaseEvent(request, requestDto.getPackageId(), requestId, requestDto.getChannelCode());
		} catch (ServiceException ex) {
			log.error("[saveEvent] ERROR: {}", ex.getMessage());
			responseUtils.setResponseError(response, ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception e){
			log.error("[saveEvent] ERROR: {}", e.getMessage());
			responseUtils.setResponseError(response, ErrorConstant.ERR_5001001, e.getMessage());
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = UrlConstant.CANCEL_EVENTS, consumes = {MediaType.APPLICATION_JSON_VALUE})
	@ValidateAnnotation
	@LogsActivityAnnotation
	public ResponseEntity<Object> cancelEvents(@Valid @RequestBody EventManagementRequestDto request, BindingResult bindingResult) {
		ResponseDto response = new ResponseDto(request.getRequestId());
		try {
			response = eventManagementService.cancelEvent(request, request.getRequestId());
		}
		catch (ServiceException ex) {
			log.error("[cancelEvents] ERROR: {}", ex.getMessage());
			responseUtils.setResponseError(response, ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception e){
			log.error("[cancelEvents] ERROR: {}", e.getMessage());
			responseUtils.setResponseError(response, ErrorConstant.ERR_5001001, null);
		}
		return ResponseEntity.ok(response);
	}

}
