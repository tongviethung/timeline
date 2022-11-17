package com.vpbanks.timeline.repository.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Document(collection = "validate_rule")
@Data
public class ValidateRuleEntity {

	@Id
	private String id;

	@Field(value = "type_group")
	private String typeGroup;

	@Field(value = "type_id")
	private String typeId;

	@Field(value = "fields_rule")
	private List<FieldRule> fieldsRule;

}