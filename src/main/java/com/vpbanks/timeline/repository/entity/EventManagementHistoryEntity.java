package com.vpbanks.timeline.repository.entity;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@Data
@Document(collection = "event_management_history")
public class EventManagementHistoryEntity {

	@Id
	private String id;

	@Field(value = "event_id")
	private String eventId;

	@Field(value = "event_type")
	private String eventType;

	@Field(value = "user_id")
	private String userId;

	@Field(value = "channel_code")
	private String channelCode;

	@Field(value = "start_date")
	private Long startDate;

	@Field(value = "end_date")
	private Long endDate;

	@Field(value = "status")
	private String status;

	@Field(value = "error_message")
	private String errorMessage;

	@Field(value = "product_code")
	private String productCode;

	@Field(value = "contract_code")
	private String contractCode;

	@Field(value = "interest_amount")
	private BigDecimal interestAmount;

	@Field(value = "principal_amount")
	private BigDecimal principalAmount;

	@Field(value = "investment_amount")
	private BigDecimal investmentAmount;

	@Field(value = "withdrawal_amount")
	private BigDecimal withDrawalAmount;

	@Field(value = "package_id")
	private String packageId;

	@Field(value = "created_date")
	private long createdDate;

	@Field(value = "created_by")
	private String createdBy;

	@Field(value = "updated_date")
	private long updatedDate;

	@Field(value = "updated_by")
	private String updatedBy;

	@Field(value = "frequency_repetition")
	private Integer frequencyRepetition;

	@Field(value = "version")
	private int version;

	@Field(value = "reference_id")
	private String referenceId;

	public EventManagementHistoryEntity(EventManagementEntity dto) {
		BeanUtils.copyProperties(dto, this);
	}

}
