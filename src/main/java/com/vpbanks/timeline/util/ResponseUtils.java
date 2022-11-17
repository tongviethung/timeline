package com.vpbanks.timeline.util;

import com.vpbanks.timeline.constants.BaseConfigConstant;
import com.vpbanks.timeline.constants.ErrorConstant;
import com.vpbanks.timeline.response.ResponseDto;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class ResponseUtils {

	private final Environment env;

	public ResponseDto setResponseError(ResponseDto responseDto, String errorCode, String errorMsg) {
		responseDto.setCode(errorCode);
		if(Strings.isBlank(errorMsg)) {
			errorMsg = getErrorMsg(errorCode);
		}
		responseDto.setMessage(errorMsg);
		responseDto.setStatus(BaseConfigConstant.RESPONSE_STATUS_FAIL);
		responseDto.setResponseTime(LocalDateTime.now());
		return responseDto;
	}

	public ResponseDto setResponseSuccess(ResponseDto responseDto, Object data) {
		responseDto.setCode(ErrorConstant.SUCCESS);
		responseDto.setData(data);
		responseDto.setStatus(BaseConfigConstant.RESPONSE_STATUS_SUCCESS);
		responseDto.setResponseTime(LocalDateTime.now());
		return responseDto;
	}

	public String getErrorMsg(String errorCode) {
		return env.getProperty(errorCode);
	}
}
