package com.vpbanks.timeline.annotation.handler;

import com.vpbanks.timeline.annotation.ValidateAnnotation;
import com.vpbanks.timeline.constants.BaseConfigConstant;
import com.vpbanks.timeline.constants.ErrorConstant;
import com.vpbanks.timeline.request.RequestDto;
import com.vpbanks.timeline.response.ResponseDto;
import com.vpbanks.timeline.util.ResponseUtils;
import lombok.AllArgsConstructor;
import net.logstash.logback.encoder.org.apache.commons.lang.math.NumberUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Aspect
@Component
@AllArgsConstructor
@Order(value = 3)
public class ValidateAnnotationHandle {

  private final ResponseUtils responseUtils;

  @Around("execution(* *(..)) && @annotation(validateAnnotation)")
  public Object makerCheckerValidateAnnotation(ProceedingJoinPoint point,
      ValidateAnnotation validateAnnotation) throws Throwable {
    // Get dataRequest
    RequestDto objectRequest = (RequestDto) point.getArgs()[0];
    BindingResult bindingResult = (BindingResult) point.getArgs()[point.getArgs().length - 1];

    // Validate data
    if (bindingResult.hasErrors()) {
      ResponseDto responseBasicObj = new ResponseDto(ErrorConstant.BAD_REQUEST, null, objectRequest.getRequestId(), BaseConfigConstant.RESPONSE_STATUS_FAIL, null);
      bindingResult.getFieldErrors().stream().forEach(f -> {
        String code = f.getDefaultMessage();
        if (!NumberUtils.isNumber(code)) {
          responseUtils.setResponseError(responseBasicObj, ErrorConstant.ERR_5001001, "");
        } else {
          responseUtils.setResponseError(responseBasicObj, code, "");
        }
      });
      return new ResponseEntity<Object>(responseBasicObj, HttpStatus.OK);
    } else {
      return point.proceed();
    }
  }
}
