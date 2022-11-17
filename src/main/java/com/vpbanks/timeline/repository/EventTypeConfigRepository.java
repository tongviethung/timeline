package com.vpbanks.timeline.repository;

import com.vpbanks.timeline.repository.entity.EventTypeConfigEntity;
import com.vpbanks.timeline.repository.entity.ValidateRuleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventTypeConfigRepository extends MongoRepository<EventTypeConfigEntity, String> {
    EventTypeConfigEntity findByChannelCodeAndEventTypeCodeAndIsActive(String channelCode, String eventTypeCode, int isActive);

    List<EventTypeConfigEntity> findByIsActive(int isActive);
}
