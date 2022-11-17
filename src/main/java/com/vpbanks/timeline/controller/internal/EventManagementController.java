package com.vpbanks.timeline.controller.internal;

import com.vpbanks.timeline.annotation.LogsActivityAnnotation;
import com.vpbanks.timeline.annotation.ValidateAnnotation;
import com.vpbanks.timeline.constants.ErrorConstant;
import com.vpbanks.timeline.constants.UrlConstant;
import com.vpbanks.timeline.request.*;
import com.vpbanks.timeline.response.ResponseDto;
import com.vpbanks.timeline.response.exception.ServiceException;
import com.vpbanks.timeline.service.EventErrorService;
import com.vpbanks.timeline.service.EventManagementService;
import com.vpbanks.timeline.util.JsonUtil;
import com.vpbanks.timeline.util.ResponseUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping(UrlConstant.V1_BASIC_ADMIN_URL + UrlConstant.EVENT_MANAGEMENT_URL)
public class EventManagementController {

	private final EventManagementService eventManagementService;

	private final ResponseUtils responseUtils;

	private final EventErrorService eventErrorService;

	/** 
	 * url: /find-events
	 * 
	 * api lấy thông tin event
	 * 
	 * **/
	@PostMapping(UrlConstant.FIND_EVENT)
	@LogsActivityAnnotation
	public ResponseEntity<Object> getEvents(@RequestBody FindEventRequestDto request) {
		log.info("[getEvents] requestId: {}", request.getRequestId());
		ResponseDto response = new ResponseDto(request.getRequestId());
		try {
			Pageable pageable = PageRequest.of(request.getPageIndex(), request.getPageSize());
			response = eventManagementService.findByParam(response, request, pageable);
		} catch (ServiceException ex) {
			log.error("[getEvents] ERROR: {}", ex.getMessage());
			responseUtils.setResponseError(response, ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception e){
			log.error("[getEvents] ERROR OUT: {}", e.getMessage());
			responseUtils.setResponseError(response, ErrorConstant.ERR_5001001, e.getMessage());
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = UrlConstant.CANCEL_EVENTS, consumes = {MediaType.APPLICATION_JSON_VALUE})
	@PreAuthorize("@securityUtils.hasRoles(@rolesConstants.TLINE_EVENT_003)")
	@ValidateAnnotation
	@LogsActivityAnnotation
	public ResponseEntity<Object> cancelEvents(@Valid @RequestBody EventManagementRequestDto request, BindingResult bindingResult) {
		String requestId = request.getRequestId();
		log.info("[cancelEvents] requestId: {}, request: {}", requestId, JsonUtil.toJsonString(request));
		ResponseDto response = new ResponseDto(requestId);
		try {
			response = eventManagementService.cancelEvent(request, requestId);
		}
		catch (ServiceException ex) {
			log.error("[cancelEvents] ERROR: {}", ex.getMessage());
			responseUtils.setResponseError(response, ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception e){
			log.error("[cancelEvents] ERROR: {}", e.getMessage());
			responseUtils.setResponseError(response, ErrorConstant.ERR_5001001, e.getMessage());
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = UrlConstant.SEND_EVENTS, consumes = {MediaType.APPLICATION_JSON_VALUE})
	@PreAuthorize("@securityUtils.hasRoles(@rolesConstants.TLINE_EVENT_002, @rolesConstants.TLINE_EVENT_004)")
	@ValidateAnnotation
	public ResponseEntity<ResponseDto> executedEvents(@RequestBody @Valid EventManagementRequestDto request, BindingResult bindingResult) {
		String requestId = request.getRequestId();
		log.info("[executedEvents] requestId: {}, request: {}", requestId, JsonUtil.toJsonString(request));
		ResponseDto response = new ResponseDto(requestId);
		try {
			response = eventManagementService.executedEvents(request, requestId);
		} catch (ServiceException ex) {
			log.error("[executedEvents] ERROR: {}", ex.getMessage());
			responseUtils.setResponseError(response, ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception e){
			log.error("[executedEvents] ERROR: {}", e.getMessage());
			responseUtils.setResponseError(response, ErrorConstant.ERR_5001001, e.getMessage());
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = UrlConstant.GET_JOB, consumes = {MediaType.APPLICATION_JSON_VALUE})
	@PreAuthorize("@securityUtils.hasRoles(@rolesConstants.TLINE_JOB_001)")
	@LogsActivityAnnotation
	public ResponseEntity<ResponseDto> getJobMonitor(@RequestBody FindEventRequestDto findEventRequestDto) {
		ResponseDto response = new ResponseDto(findEventRequestDto.getRequestId());
		try {
			response = eventManagementService.getJobMonitor(findEventRequestDto);
		} catch (ServiceException ex) {
			log.error("[getJobMonitor] serviceException ERROR: {}", ex.getMessage());
			responseUtils.setResponseError(response, ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception e){
			log.error("[getJob] exception ERROR: {}", e.getMessage());
			responseUtils.setResponseError(response, ErrorConstant.ERR_5001001, e.getMessage());
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = UrlConstant.GET_EVENT_TYPE, consumes = {MediaType.APPLICATION_JSON_VALUE})
	@LogsActivityAnnotation
	public ResponseEntity<ResponseDto> getEventType(@RequestBody RequestDto request) {
		ResponseDto response = new ResponseDto(request.getRequestId());
		try {
			response = eventManagementService.getEventType(request.getRequestId());
		} catch (Exception e){
			log.error("[getEventType] exception ERROR: {}", e.getMessage());
			responseUtils.setResponseError(response, ErrorConstant.ERR_5001001, e.getMessage());
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = UrlConstant.GET_CHANNEL_CODE, consumes = {MediaType.APPLICATION_JSON_VALUE})
	@LogsActivityAnnotation
	public ResponseEntity<ResponseDto> getChannelCode(@RequestBody RequestDto request) {
		ResponseDto response = new ResponseDto(request.getRequestId());
		try {
			response = eventManagementService.getChannelCode(request.getRequestId());
		} catch (Exception e){
			log.error("[getChannelCode] exception ERROR: {}", e.getMessage());
			responseUtils.setResponseError(response, ErrorConstant.ERR_5001001, e.getMessage());
		}
		return ResponseEntity.ok(response);
	}
}
