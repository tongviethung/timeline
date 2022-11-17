package com.vpbanks.timeline.repository;

import com.vpbanks.timeline.repository.entity.EventErrorEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventErrorRepository extends MongoRepository<EventErrorEntity, String> {

}
