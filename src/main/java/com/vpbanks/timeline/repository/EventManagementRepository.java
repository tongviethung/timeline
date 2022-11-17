package com.vpbanks.timeline.repository;

import com.vpbanks.timeline.repository.entity.JobMonitorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.vpbanks.timeline.repository.entity.EventManagementEntity;

import java.util.List;

public interface EventManagementRepository extends MongoRepository<EventManagementEntity, String> {

	@Query(value = "{'timeStart' : {$lt: ?1, $gte: ?0}, 'status' : {$in :  ?2}, 'isActive' : ?3}", count = true)
	long countByScantEvent(long timeStart, long timeEnd, List<String> statuses, int isActive);

	@Query(value = "{'timeStart' : {$lt: ?1, $gte: ?0}, 'status' : {$in :  ?2}, 'isActive' : ?3}")
    Page<EventManagementEntity> findByScantEvent(long timeStart, long timeEnd, List<String> statuses, int isActive, Pageable pageable);

    @Query(value = "{'id' : {\"$gt\" : ?0}, 'id' : {\"$lt\" : ?1}}", count = true)
    long countById(long idBegin, long idEnd);
	
	@Query("{'timeStart' : {$gte: ?0, $lte: ?1}, "
			+ "$or : [{'userId' : ?2}, {'productId' : ?3}, {'channelCode' : ?4}, {'eventType' : ?5}] }")
	Page<EventManagementEntity> findByParam(
			Long fromDate, 
			Long todate,
			String userId,
			String productId,
			String channelCode,
			String eventType,
			Pageable pageable);

	EventManagementEntity findByReferenceIdAndChannelCode(String referenceId, String channelCode);

    List<EventManagementEntity> findByPackageIdAndStatusAndIsActive(String packageId, String status, int isActive);
}
