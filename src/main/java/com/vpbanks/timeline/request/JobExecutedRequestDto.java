package com.vpbanks.timeline.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JobExecutedRequestDto extends RequestDto{

    @JsonProperty("time_start")
    long timeStart;

    @JsonProperty("time_end")
    long timeEnd;
}
