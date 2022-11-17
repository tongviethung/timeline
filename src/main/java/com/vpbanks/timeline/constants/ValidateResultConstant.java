package com.vpbanks.timeline.constants;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidateResultConstant {
  private String code;
  private String message;

  public static ValidateResultConstant buildRequireFieldError(String field) {
    return new ValidateResultConstant(ErrorConstant.TimeLineErrorCode.FIELD_REQUIRED.getCode(), "Field " + field + " is require");
  }

  public static ValidateResultConstant buildNotAllowFieldError(String field, Object value) {
    return new ValidateResultConstant(field + "_not_allow", "Field " + field + " is not allow");
  }

  public static ValidateResultConstant buildInvalidPatternError(String field, Object value) {
    return new ValidateResultConstant(field + "_invalid", "Field " + field + " is invalid format");
  }

  public static ValidateResultConstant buildNotInDataListError(String field, Object value) {
    return new ValidateResultConstant(field + "_invalid", "Field " + field + " not in available data list");
  }

  public static ValidateResultConstant buildDuplicateConstraint(String field, Object value) {
    return new ValidateResultConstant(field + "_invalid", "Field " + field + " duplicate with other");
  }
}
