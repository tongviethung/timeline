package com.vpbanks.timeline.repository;

import com.vpbanks.timeline.repository.entity.ChannelCodeEntity;
import com.vpbanks.timeline.repository.entity.HungTestEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HungTestRepository extends MongoRepository<HungTestEntity, String> {
}
