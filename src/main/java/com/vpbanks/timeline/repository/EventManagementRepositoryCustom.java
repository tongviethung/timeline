package com.vpbanks.timeline.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.vpbanks.timeline.repository.entity.JobMonitorEntity;
import com.vpbanks.timeline.request.FindEventRequestDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.vpbanks.timeline.repository.entity.EventManagementEntity;
import com.vpbanks.timeline.response.EventManagementResponse;
import com.vpbanks.timeline.response.PageCustom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class EventManagementRepositoryCustom {
	
    private final MongoTemplate mongoTemplate;
	
	public PageCustom<EventManagementResponse> findEvents(Long fromDate, Long toDate, FindEventRequestDto request, Pageable pageable) {
		log.info("[findEvents] START");
		try {
			Query query = new Query();
			List<Criteria> listCriteria = new ArrayList<>();
			if (Objects.nonNull(fromDate))
			listCriteria.add(Criteria.where("time_start").gte(fromDate));
			if (Objects.nonNull(toDate))
			listCriteria.add(Criteria.where("time_start").lte(toDate));
			if (StringUtils.hasLength(request.getEventId())) {
				listCriteria.add(Criteria.where("id").is(request.getEventId()));
			}
			if (StringUtils.hasLength(request.getUserId())) {
				listCriteria.add(Criteria.where("user_id").is(request.getUserId()));
			}
			if (StringUtils.hasLength(request.getProductId())) {
				listCriteria.add(Criteria.where("product_code").is(request.getProductId()));
			}
			if (StringUtils.hasLength(request.getChannelCode())) {
				listCriteria.add(Criteria.where("channel_code").is(request.getChannelCode()));
			}
			if (StringUtils.hasLength(request.getEventType())) {
				listCriteria.add(Criteria.where("event_type").is(request.getEventType()));
			}
			if (StringUtils.hasLength(request.getStatus())) {
				listCriteria.add(Criteria.where("status").is(request.getStatus()));
			}
			if (CollectionUtils.isEmpty(listCriteria)) {
				query.addCriteria(new Criteria());
			} else {
				query.addCriteria(new Criteria().andOperator(listCriteria.toArray(new Criteria[listCriteria.size()])));
			}
			int count = Math.toIntExact(mongoTemplate.count(query, EventManagementEntity.class));
			int page = count%pageable.getPageSize() > 0 ? count/pageable.getPageSize() + 1 : count/pageable.getPageSize();
			query.with(pageable);
			List<EventManagementEntity> eventManagementEntities = mongoTemplate.find(query, EventManagementEntity.class);
			List<EventManagementResponse> result = eventManagementEntities.stream().map(EventManagementResponse::new).collect(Collectors.toList());
			return new PageCustom<EventManagementResponse>(page, Long.valueOf(count), result);
		} catch (Exception e) {
			log.error("[findEvents] ERROR OUT: {}", e.getMessage());
			return null;
		}
	}

	public PageCustom<JobMonitorEntity> getJob(Long fromDate, Long toDate, 
											   String status,Long id, Long countEvent, Pageable pageable) {
		log.info("[getJob] START");
		try {
			Query query = new Query();
			List<Criteria> listCriteria = new ArrayList<>();
			listCriteria.add(Criteria.where("time_start").gte(fromDate));
			listCriteria.add(Criteria.where("time_end").lte(toDate));
			if (StringUtils.hasLength(status)) {
				listCriteria.add(Criteria.where("status").is(status));
			}
			if (id != null) {
				listCriteria.add(Criteria.where("_id").is(id));
			}
			if (countEvent != null) {
				listCriteria.add(Criteria.where("count_event").is(countEvent));
			}
			query.addCriteria(new Criteria().andOperator(listCriteria.toArray(new Criteria[listCriteria.size()])));
			int count = Math.toIntExact(mongoTemplate.count(query, JobMonitorEntity.class));
			int page = count%pageable.getPageSize() > 0 ? count/pageable.getPageSize() + 1 : count/pageable.getPageSize();
			query.with(pageable);
			List<JobMonitorEntity> jobMonitor = mongoTemplate.find(query, JobMonitorEntity.class);
			return new PageCustom<>(page, Long.valueOf(count), jobMonitor);
		} catch (Exception e) {
			log.error("[getJob] ERROR OUT: {}", e.getMessage());
			return null;
		}
	}
}
