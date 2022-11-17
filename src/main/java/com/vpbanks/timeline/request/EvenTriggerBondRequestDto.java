package com.vpbanks.timeline.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vpbanks.timeline.repository.entity.EventManagementEntity;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Data
public class EvenTriggerBondRequestDto {

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

	@JsonProperty("version")
	private Integer version;

	@JsonProperty("user_id")
	private String userId;

	@JsonProperty("error_message")
	private String errorMessage;

	@JsonProperty("contract_code")
	private String contractCode;

	@JsonProperty("interest_amount")
	private BigDecimal interestAmount;

	@JsonProperty("principal_amount")
	private BigDecimal principalAmount;

	@JsonProperty("investment_amount")
	private BigDecimal investmentAmount;

	@JsonProperty("withdrawal_amount")
	private BigDecimal withDrawalAmount;

	@JsonProperty("package_id")
	private String packageId;

	@JsonProperty("created_date")
	private long createdDate;

	@JsonProperty("created_by")
	private String createdBy;

	@JsonProperty("updated_date")
	private long updatedDate;

	@JsonProperty("updated_by")
	private String updatedBy;

	public EvenTriggerBondRequestDto(EventManagementEntity entity) {
		BeanUtils.copyProperties(entity,this);
	}
}