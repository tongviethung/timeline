package com.vpbanks.timeline.repository;

import com.vpbanks.timeline.repository.entity.JobMonitorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface JobMonitorRepository extends MongoRepository<JobMonitorEntity, String> {
    @Query("{'time_start' : {$gte: ?0}, 'time_end' : {$lte: ?1} ,"
            + "$or : [{'status' : ?2}]}")
    Page<JobMonitorEntity> findJobMonitorEntity(
            long timeStart,
            long timeEnd,
            String status,
            Pageable pageable
    );

    @Query("{'id' : ?0, 'status' : ?1}")
    Optional<JobMonitorEntity> findByIdAndStatus(String id, String status);
}
