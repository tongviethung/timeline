package com.vpbanks.timeline.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobMonitorRequestDto extends RequestDto{
    private String fromDate;
    private String toDate;
    private String status;
}
