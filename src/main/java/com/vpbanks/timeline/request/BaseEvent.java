package com.vpbanks.timeline.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BaseEvent {

	@JsonProperty("reference_id")
	private String referenceId;

	@JsonProperty("event_type")
	private String eventType;
	
	@JsonProperty("start_date")
	private Long startDate;
	
	@JsonProperty("end_date")
	private Long endDate;

	@JsonProperty("frequency_repetition")
	private Integer frequencyRepetition;

	@JsonProperty("status")
	private String status;

	@JsonProperty("product_code")
	private String productCode;

	//COPYTRADE
	@JsonProperty("issue_method_code")
	private String issueMethodCode;
	//COPYTRADE

	//BOND
	@JsonProperty("user_id")
	private String userId;

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
	//BOND
}
