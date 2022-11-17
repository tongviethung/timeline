package com.vpbanks.timeline.repository;

import com.vpbanks.timeline.repository.entity.EventManagementHistoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventManagementHistoryRepository extends MongoRepository<EventManagementHistoryEntity, String> {

}
