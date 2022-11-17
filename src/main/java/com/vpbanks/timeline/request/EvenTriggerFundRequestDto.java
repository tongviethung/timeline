package com.vpbanks.timeline.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vpbanks.timeline.repository.entity.EventManagementEntity;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class EvenTriggerFundRequestDto {

	private String id;

	@JsonProperty("time_start")
	private Long timeStart;

	@JsonProperty("time_end")
	private Long timeEnd;

	@JsonProperty("event_type")
	private String eventType;

	@JsonProperty("channel_code")
	private String channelCode;

	@JsonProperty("frequency_repetition")
	private Integer frequencyRepetition;

	@JsonProperty("reference_id")
	private String referenceId;

	@JsonProperty("product_code")
	private String productCode;

	@JsonProperty("created_date")
	private long createdDate;

	@JsonProperty("created_by")
	private String createdBy;

	@JsonProperty("updated_date")
	private long updatedDate;

	@JsonProperty("updated_by")
	private String updatedBy;

	@JsonProperty("version")
	private Integer version;

	public EvenTriggerFundRequestDto(EventManagementEntity entity) {
		BeanUtils.copyProperties(entity,this);
	}
}