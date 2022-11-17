package com.vpbanks.timeline.annotation.handler;

import static net.logstash.logback.argument.StructuredArguments.entries;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vpbanks.timeline.annotation.LogsActivityAnnotation;
import com.vpbanks.timeline.request.RequestDto;
import com.vpbanks.timeline.response.ResponseDto;
import com.vpbanks.timeline.util.CommonFormatUtil;
import com.vpbanks.timeline.util.JsonUtil;

/** The Class LogsActivityAOPHandle. */
@Aspect
@Component
@Order(value = 2)
public class LogsActivityAopHandler {

	private final String INTERNAL_PROCESS = "internal-process";

	/** The logger. */
	private Logger logger = LoggerFactory.getLogger(LogsActivityAopHandler.class);

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private JsonUtil jsonUtil;

	@Value("${log.sensitive.key}")
	private String pIIKeys;

	/**
	 * Logs activity annotation.
	 *
	 * @param point                  the point
	 * @param logsActivityAnnotation the logs activity annotation
	 * @return the object
	 * @throws Throwable the throwable
	 */
	@Around("execution(* *(..)) && @annotation(logsActivityAnnotation)")
	public Object logsActivityAnnotation(ProceedingJoinPoint point, LogsActivityAnnotation logsActivityAnnotation)
			throws Throwable {
		// Get dataRequest
		Map<String, Object> mapCustomizeLog = new HashMap<>();
		String requestId = null;
		String requestUri = null;

		List<String> redactKeys = Arrays.asList(pIIKeys.split(","));

		try {
			// Get first parameter of function. it always request data.
			final Object objectRequest = point.getArgs()[0];
			// Get second parameter of function. it may be target Uri of
			// restTemplate.exchange
			String targetPath = null;
			if (point.getArgs().length >= 2) {
				try {
					String strPath = String.valueOf(point.getArgs()[1]);
					if (strPath.contains("/")) {
						targetPath = strPath;
					}
				} catch (Exception e) {
					logger.error(e.toString());
				}
			} else {
				targetPath = INTERNAL_PROCESS;
			}

			try {
				requestUri = httpServletRequest.getRequestURI();
			} catch (Exception e) {
				requestUri = INTERNAL_PROCESS;
			}
			mapCustomizeLog.put("request_path", requestUri);
			mapCustomizeLog.put("target_path", targetPath);
			mapCustomizeLog.put("code_file", point.getSignature().getDeclaringTypeName());
			mapCustomizeLog.put("method_name", point.getSignature().getName());
			mapCustomizeLog.put("message_type", "request");

			if (objectRequest instanceof RequestDto) {
				// Log of Controller with normal request
				RequestDto basicRequestObj = (RequestDto) objectRequest;
				requestId = basicRequestObj.getRequestId();
				mapCustomizeLog.put("request_id", requestId);
				logger.info(CommonFormatUtil.redact(JsonUtil.toJsonString(objectRequest), redactKeys),
						entries(mapCustomizeLog));
			} else {
				logger.info(CommonFormatUtil.redact(JsonUtil.toJsonString(objectRequest), redactKeys),
						entries(mapCustomizeLog));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}

		// Time start function
		long timeStart = new Date().getTime();
		// Set dataResponse
		final Object objectResponse = point.proceed();

		try {
			// Time handle
			long timeHandle = new Date().getTime() - timeStart;
			mapCustomizeLog.put("execution_time", timeHandle);
			mapCustomizeLog.put("code_file", point.getSignature().getDeclaringTypeName());
			mapCustomizeLog.put("method_name", point.getSignature().getName());
			mapCustomizeLog.put("request_id", requestId);
			mapCustomizeLog.put("message_type", "response");

			if (objectResponse == null) {
				return null;
			} else if (objectResponse instanceof ResponseEntity) {
				ResponseEntity<?> responseEntity = (ResponseEntity<?>) objectResponse;
				mapCustomizeLog.put("status_code", responseEntity.getStatusCode().value());

				// Check if response to vinid
				ResponseDto responseBasicObj = jsonUtil.getEntityFromJsonObj(responseEntity.getBody(),
						ResponseDto.class);
				mapCustomizeLog.put("error_code", String.valueOf(responseBasicObj.getCode()));
				logger.info(CommonFormatUtil.redact(JsonUtil.toJsonString(responseBasicObj), redactKeys),
						entries(mapCustomizeLog));

			} else {
				logger.info(CommonFormatUtil.redact(JsonUtil.toJsonString(objectResponse), redactKeys),
						entries(mapCustomizeLog));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}

		// Continue response
		return objectResponse;
	}
}
