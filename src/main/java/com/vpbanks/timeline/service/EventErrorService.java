package com.vpbanks.timeline.service;

import com.vpbanks.timeline.repository.entity.EventErrorEntity;

import java.util.List;

public interface EventErrorService {

	void saveEventErrors(List<EventErrorEntity> eventErrors);
}
