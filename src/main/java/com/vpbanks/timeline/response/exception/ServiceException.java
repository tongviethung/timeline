package com.vpbanks.timeline.response.exception;

import com.vpbanks.timeline.constants.ErrorConstant.TimeLineErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private final String errorCode;
	private final String errorMessage;

	public ServiceException(TimeLineErrorCode error) {
		this.errorCode = error.getCode();
		this.errorMessage = error.getMessage();
	}

	public ServiceException(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}
