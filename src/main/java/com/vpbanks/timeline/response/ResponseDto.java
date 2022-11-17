package com.vpbanks.timeline.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * The Class ResponseDto.
 */
@Data
public class ResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String requestId;

	private int status; // 1: Success, 0: Fail

	private String code;

	private String message;

	private Object data;

	private int totalError;

	@JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
	private LocalDateTime responseTime;

	public ResponseDto() {
	}

	public ResponseDto(String requestId) {
		this.requestId = requestId;
	}

	public ResponseDto(String code, String message, String requestId, int status, Object data) {
		this.requestId = requestId;
		this.status = status;
		this.code = code;
		this.message = message;
		this.responseTime = LocalDateTime.now();
		this.data = data;
	}

	public ResponseDto(String code, String message, String requestId, int status, Object data, int totalErr) {
		this.requestId = requestId;
		this.status = status;
		this.code = code;
		this.message = message;
		this.totalError = totalErr;
		this.responseTime = LocalDateTime.now();
		this.data = data;
	}

}
