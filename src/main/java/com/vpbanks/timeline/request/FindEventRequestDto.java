package com.vpbanks.timeline.request;

import brave.Request;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Getter
@Setter
public class FindEventRequestDto extends RequestDto {

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("from_date")
    private String fromDate;

    @JsonProperty("to_date")
    private String toDate;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("channel_code")
    private String channelCode;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("status")
    private String status;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("count_event")
    private Long countEvent;
}
