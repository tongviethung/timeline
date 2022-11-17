package com.vpbanks.timeline.service.impl;

import com.vpbanks.timeline.repository.EventErrorRepository;
import com.vpbanks.timeline.repository.entity.EventErrorEntity;
import com.vpbanks.timeline.service.EventErrorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class EventErrorServiceImpl implements EventErrorService {

	private final EventErrorRepository eventErrorRepository;

	@Override
	public void saveEventErrors(List<EventErrorEntity> eventErrors) {
		eventErrorRepository.saveAll(eventErrors);
	}
}
