package com.vpbanks.timeline.repository.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class FieldRule{

	private String field;
	
	@Field(value = "is_required")
	private Integer isRequired;

}
