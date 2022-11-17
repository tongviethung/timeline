package com.vpbanks.timeline.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StopAllJobRequestDto extends RequestDto {
    private String monitorId;
}
