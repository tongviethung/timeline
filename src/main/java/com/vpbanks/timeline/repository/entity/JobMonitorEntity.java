package com.vpbanks.timeline.repository.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "job_monitor")
@Builder
public class JobMonitorEntity {

	@Id
	private String id;

	@Field(value = "total_event")
	private long totalEvent;

	@Field(value = "status")
	private String status;

	@Field(value = "time_start")
	private long timeStart;

	@Field(value = "time_end")
	private long timeEnd;

	@Field(value = "time_execute")
	private long timeExecute;

	@Field(value = "total_event_error")
	private long totalEventError;

	@Field(value = "total_event_success")
	private long totalEventSuccess;

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
}
