package com.vpbanks.timeline.response;

import com.vpbanks.timeline.constants.BondEventTypeEnum;
import com.vpbanks.timeline.repository.entity.EventManagementEntity;
import com.vpbanks.timeline.util.DateUtils;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
public class EventManagementResponse {

	private String id;
	private String eventType;
	private String eventTypeName;
	private String userId;
	private String timeStart;
	private String timeEnd;
	private String status;
	private String chanelCode;
	private String productCode;
	private String contractCode;
	private int isActive;
	private BigDecimal interestAmount;
	private BigDecimal principalAmount;
	private BigDecimal investmentAmount;
	private BigDecimal withDrawalAmount;
	private String packageId;
	private String createdDate;
	private String createdBy;
	private String updatedDate;
	private String updatedBy;

	private Integer frequencyRepetition;

	private Integer version;
	
	public EventManagementResponse(EventManagementEntity e) {
		this.id = e.getId();
		BondEventTypeEnum eventTypeCommon = BondEventTypeEnum.lockUpByName(e.getEventType());
		if (Objects.nonNull(eventTypeCommon)) {
			this.eventType = eventTypeCommon.name();
			this.eventTypeName = eventTypeCommon.getValue();
		}
		this.userId = e.getUserId();
		this.timeStart = DateUtils.convertDateToString(new Date(e.getTimeStart()), DateUtils.FORMAT_DDMMYYYYHHMMSS);
		this.timeEnd = Objects.nonNull(e.getTimeEnd()) ? DateUtils.convertDateToString(new Date(e.getTimeEnd()), DateUtils.FORMAT_DDMMYYYYHHMMSS) : null;
		this.status = e.getStatus();
		this.chanelCode = e.getChannelCode();
		this.productCode = e.getProductCode();
		this.contractCode = e.getContractCode();
		this.isActive = e.getIsActive();
		this.interestAmount = e.getInterestAmount();
		this.principalAmount = e.getPrincipalAmount();
		this.investmentAmount = e.getInvestmentAmount();
		this.withDrawalAmount = e.getWithDrawalAmount();
		this.packageId = e.getPackageId();
		this.createdDate = DateUtils.convertDateToString(new Date(e.getCreatedDate()), DateUtils.FORMAT_DDMMYYYYHHMMSS);
		this.createdBy = e.getCreatedBy();
		this.updatedDate = DateUtils.convertDateToString(new Date(e.getUpdatedDate()), DateUtils.FORMAT_DDMMYYYYHHMMSS);
		this.updatedBy = e.getUpdatedBy();
		this.frequencyRepetition = e.getFrequencyRepetition();
		this.version = e.getVersion();
	}
}
