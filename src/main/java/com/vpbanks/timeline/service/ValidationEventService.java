package com.vpbanks.timeline.service;

import com.vpbanks.timeline.constants.ValidateResultConstant;

import java.util.List;

public interface ValidationEventService<T> {
	List<ValidateResultConstant> validateCommonByEventType(String typeGroup, String typeId, T obj) throws Exception;

}
