package com.vpbanks.timeline.repository;

import com.vpbanks.timeline.repository.entity.EventManagementEntity;
import com.vpbanks.timeline.repository.entity.ValidateRuleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Objects;

public interface ValidateRuleRepository extends MongoRepository<ValidateRuleEntity, String> {

    ValidateRuleEntity findByTypeGroupAndTypeId(String typeGroup, String typeId);

    List<ValidateRuleEntity> findByTypeGroup(String typeGroup);
}
