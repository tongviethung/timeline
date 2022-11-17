package com.vpbanks.timeline.repository.entity;

import com.vpbanks.timeline.constants.BaseConfigConstant;
import com.vpbanks.timeline.request.BaseEvent;
import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@Data
@Document(collection = "event_management")
public class EventManagementEntity {

	@Id
	private String id;

	@Field(value = "time_start")
	private Long timeStart;

	@Field(value = "time_end")
	private Long timeEnd;

	@Field(value = "status")
	private String status;

	@Field(value = "error_message")
	private String errorMessage;

	@Field(value = "event_type")
	private String eventType;

	@Field(value = "channel_code")
	private String channelCode;

	@Field(value = "product_code")
	private String productCode;

	@Field(value = "is_active")
	private int isActive;

	@Field(value = "created_date")
	@CreatedDate
	private long createdDate;

	@Field(value = "created_by")
	@CreatedBy
	private String createdBy;

	@Field(value = "updated_date")
	@LastModifiedDate
	private long updatedDate;

	@Field(value = "updated_by")
	@LastModifiedBy
	private String updatedBy;

	//COPYTRADE
	@Field(value = "issue_method_code")
	private String issueMethodCode;
	//COPYTRADE

	@Field(value = "frequency_repetition")
	private Integer frequencyRepetition;

	@Field(value = "version")
	@Version
	private Integer version;

	@Field(value = "reference_id")
	private String referenceId;

	//BOND
	@Field(value = "user_id")
	private String userId;

	@Field(value = "package_id")
	private String packageId;

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
	//BOND

	public EventManagementEntity() {
	}

	public EventManagementEntity(BaseEvent event, String packageId, String channelCode) {
		this.referenceId = event.getReferenceId();
		this.channelCode = channelCode;
		this.timeStart = event.getStartDate();
		this.timeEnd = event.getEndDate();
		this.status = BaseConfigConstant.StatusEnum.WAITING.getValue();
		this.eventType = event.getEventType();
		this.productCode = event.getProductCode();
		this.isActive = BaseConfigConstant.ACTIVE;
		this.frequencyRepetition = event.getFrequencyRepetition();

		this.issueMethodCode = event.getIssueMethodCode();

		this.packageId = packageId;
		this.userId = event.getUserId();
		this.contractCode = event.getContractCode();
		this.interestAmount = event.getInterestAmount();
		this.principalAmount = event.getPrincipalAmount();
		this.investmentAmount = event.getInvestmentAmount();
		this.withDrawalAmount = event.getWithDrawalAmount();
	}

}