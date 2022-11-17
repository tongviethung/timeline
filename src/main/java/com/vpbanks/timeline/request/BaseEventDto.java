package com.vpbanks.timeline.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BaseEventDto {

	@NotNull(message = "400002")
	@NotEmpty(message = "400002")
	@JsonProperty("package_id")
	private String packageId;

	@JsonProperty("request_id")
	private String requestId;

	@JsonProperty("channel_code")
	private String channelCode;

	private List<BaseEvent> events;
}
