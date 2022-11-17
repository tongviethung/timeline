package com.vpbanks.timeline.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KafkaRequestDto<T> {

    @JsonProperty("payload")
    public T payload;

    @JsonProperty("request_id")
    public String requestId;

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public T getPayload() {
        return this.payload;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

}
