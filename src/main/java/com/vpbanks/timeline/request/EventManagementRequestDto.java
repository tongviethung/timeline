package com.vpbanks.timeline.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EventManagementRequestDto extends RequestDto {
    @JsonProperty("id")
    @NotEmpty(message = "400003")
    private List<String> id;

    @JsonProperty("reference_id")
    private List<String> referenceId;

    @JsonProperty("user_id")
    private List<String> userId;

    @JsonProperty("product_code")
    private List<String> productCode;

    @JsonProperty("contract_code")
    private List<String> contractCode;

    @JsonProperty("event_type")
    private List<String> eventType;

    @JsonProperty("channel_code")
    private String channelCode;

    @JsonProperty("product_type")
    private String productType;

}
