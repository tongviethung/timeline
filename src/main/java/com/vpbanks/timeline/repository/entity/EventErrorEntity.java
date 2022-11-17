package com.vpbanks.timeline.repository.entity;

import com.vpbanks.timeline.constants.BaseConfigConstant;
import com.vpbanks.timeline.request.BaseEvent;
import com.vpbanks.timeline.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Document(collection = "event_error")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventErrorEntity {

	@Id
	private String id;

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

	@Field(value = "error_message")
	private String errorMessage;

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

	@Field(value = "reference_id")
	private String referenceId;

	public EventErrorEntity(BaseEvent event, String packageId, String errorMessage, String channelCode) {
		this.status = BaseConfigConstant.StatusEnum.ERROR.getValue();
		this.errorMessage = errorMessage;
		this.channelCode = channelCode;
		this.eventType = event.getEventType();
		this.userId = event.getUserId();
		this.startDate = event.getStartDate();
		this.endDate = event.getEndDate();
		this.interestAmount = event.getInterestAmount();
		this.principalAmount = event.getPrincipalAmount();
		this.investmentAmount = event.getInvestmentAmount();
		this.withDrawalAmount = event.getWithDrawalAmount();
		this.packageId = packageId;
		this.referenceId = event.getReferenceId();
		this.frequencyRepetition = event.getFrequencyRepetition();
		this.createdDate = DateUtils.localDateTimeToDate(DateUtils.convertDateToLocalDateTime(new Date())).getTime();
		this.updatedDate = DateUtils.localDateTimeToDate(DateUtils.convertDateToLocalDateTime(new Date())).getTime();
	}

}
