package com.vpbanks.timeline.repository;

import com.vpbanks.timeline.repository.entity.ChannelCodeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChannelCodeRepository extends MongoRepository<ChannelCodeEntity, String> {
}
