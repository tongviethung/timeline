/**
 * hungtv30 Apr 20, 2022
 *
 */
package com.vpbanks.timeline.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author hungtv30
 *
 */
@Data
public class EventResultRequestDto {

  @JsonProperty("id")
  private String id;
  
  @JsonProperty("status")
  private String status;

  @JsonProperty("error_message")
  private String errorMessage;
}
