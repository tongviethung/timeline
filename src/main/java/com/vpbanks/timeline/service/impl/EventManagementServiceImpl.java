package com.vpbanks.timeline.service.impl;

import com.vpbanks.timeline.annotation.LogsActivityAnnotation;
import com.vpbanks.timeline.constants.BaseConfigConstant;
import com.vpbanks.timeline.constants.ErrorConstant;
import com.vpbanks.timeline.constants.ErrorConstant.TimeLineErrorCode;
import com.vpbanks.timeline.constants.ValidateResultConstant;
import com.vpbanks.timeline.repository.*;
import com.vpbanks.timeline.repository.entity.EventErrorEntity;
import com.vpbanks.timeline.repository.entity.EventManagementEntity;
import com.vpbanks.timeline.repository.entity.EventManagementHistoryEntity;
import com.vpbanks.timeline.repository.entity.JobMonitorEntity;
import com.vpbanks.timeline.request.*;
import com.vpbanks.timeline.response.EventManagementResponse;
import com.vpbanks.timeline.response.PageCustom;
import com.vpbanks.timeline.response.ResponseDto;
import com.vpbanks.timeline.response.exception.ServiceException;
import com.vpbanks.timeline.service.EventErrorService;
import com.vpbanks.timeline.service.EventManagementService;
import com.vpbanks.timeline.service.ValidationEventService;
import com.vpbanks.timeline.service.kafka.KafkaProduceService;
import com.vpbanks.timeline.util.DateUtils;
import com.vpbanks.timeline.util.JsonUtil;
import com.vpbanks.timeline.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventManagementServiceImpl implements EventManagementService {

    private final EventManagementRepository eventManagementRepository;

    private final EventManagementHistoryRepository eventManagementHistoryRepository;

    private final EventErrorRepository eventErrorRepository;

    private final EventTypeConfigRepository eventTypeConfigRepository;

    private final ResponseUtils responseUtils;

    private final ValidationEventService<BaseEvent> validationEventService;

    private final MongoTemplate mongoTemplate;

    private final KafkaProduceService kafkaProduceService;

    private final EventErrorService eventErrorService;

    @Value("${spring.kafka.producer.timeline.trigger.event.bond}")
    private String topicEventBond;

    @Value("${spring.kafka.producer.timeline.trigger.event.copytrade}")
    private String topicEventCopytrade;

    @Value("${spring.kafka.producer.timeline.trigger.event.fund}")
    private String topicEventFund;
    
    private final EventManagementRepositoryCustom eventManagementRepositoryCustom;

    private final ChannelCodeRepository channelCodeRepo;

    @Override
    public void consumerEventResult(EventResultRequestDto request) {
        Optional<EventManagementEntity> optional = eventManagementRepository.findById(request.getId());
        if (optional.isPresent()) {
            EventManagementEntity entity = optional.get();

            entity.setStatus(request.getStatus());
            entity.setErrorMessage(request.getErrorMessage());

            this.saveEventManagementEntity(entity);
        }
    }

    @Override
    public EventManagementEntity saveEventManagementEntity(EventManagementEntity entity) {
        eventManagementRepository.save(entity);

        EventManagementHistoryEntity eventHistory = new EventManagementHistoryEntity(entity);
        eventHistory.setId(null);
        eventHistory.setEventId(String.valueOf(entity.getId()));
        eventManagementHistoryRepository.save(eventHistory);

        return entity;
    }

    @Override
    public void saveListEventManagementEntity(List<EventManagementEntity> eventManagementEntities) {
        eventManagementRepository.saveAll(eventManagementEntities);

        List<EventManagementHistoryEntity> historyEntities = new ArrayList<>();
        for(EventManagementEntity entity : eventManagementEntities){
            EventManagementHistoryEntity eventHistory = new EventManagementHistoryEntity(entity);
            eventHistory.setId(null);
            eventHistory.setEventId(String.valueOf(entity.getId()));

            historyEntities.add(eventHistory);
        }

        eventManagementHistoryRepository.saveAll(historyEntities);
    }

    @Override
	public ResponseDto findByParam(ResponseDto response, FindEventRequestDto request, Pageable pageable) throws ServiceException {
        log.info("[findByParam] request: {}", JsonUtil.toJsonString(request));
        Long fromTime = convertStrDateToLong(request.getFromDate(), TimeLineErrorCode.FROM_DATE_REQUIRED);
        Long toTime = convertStrDateToLong(request.getToDate(), TimeLineErrorCode.TO_DATE_REQUIRED);
		PageCustom<EventManagementResponse> result = eventManagementRepositoryCustom.findEvents(fromTime, toTime, request, pageable);

        log.info("[findByParam] userId: {} END", request.getUserId());
        return responseUtils.setResponseSuccess(response, result);
    }
    
    private Long convertStrDateToLong(String input, TimeLineErrorCode errorCode) throws ServiceException {
    	if (!StringUtils.hasLength(input))
    		return null;
    	return DateUtils.convertStringDateToLong(input, DateUtils.FORMAT_DDMMYYYY);
    }

    @Override
    @LogsActivityAnnotation
    public ResponseDto handleBaseEvent(List<BaseEvent> events, String packageId, String requestId, String channelCode){
        log.info("[handleBaseEvent] START requestId : {}", requestId);

        if(packageId!=null && channelCode.equals(BaseConfigConstant.ChannelCodeEnum.BOND_SERVICE.getValue())){
            handlePackageIdExist(packageId);
        }

        List<EventManagementEntity> listEvent = new ArrayList<>();
        List<EventErrorEntity> eventErrors = new ArrayList<>();

        if (!CollectionUtils.isEmpty(events)) {
            Long now = DateUtils.convertNowToLong();
            for (BaseEvent eventRequest : events) {
                try {
                    validateEvent(eventRequest, eventRequest.getStartDate(), eventRequest.getEndDate(), now, channelCode);

                    EventManagementEntity entity = new EventManagementEntity();
                    if(channelCode.equals(BaseConfigConstant.ChannelCodeEnum.BOND_SERVICE.getValue())){
                        entity = doExecuteEventNew(eventRequest.getStartDate(), now, eventRequest, packageId, channelCode, requestId);
                        listEvent.add(entity);
                    }else{
                        EventManagementEntity eventManagementOld = eventManagementRepository.findByReferenceIdAndChannelCode(eventRequest.getReferenceId(),
                                channelCode);
                        if(ObjectUtils.isEmpty(eventManagementOld)){
                            entity = doExecuteEventNew(eventRequest.getStartDate(), now, eventRequest, packageId, channelCode, requestId);
                        }else{
                            entity = doExecuteEventOld(eventRequest.getStartDate(), now, eventRequest, eventManagementOld, requestId);
                        }
                    }
                    listEvent.add(entity);

                } catch (ServiceException e) {
                    log.error("[handleEvent] ServiceException packageId: {} ERROR OUT: {}", packageId, e.getMessage());
                    eventErrors.add(new EventErrorEntity(eventRequest, packageId, String.format("%s: %s", e.getErrorCode(), e.getErrorMessage()), channelCode));
                } catch (Exception e) {
                    log.error("[handleEvent] Exception packageId: {} ERROR OUT: {}", packageId, e.getMessage());
                    eventErrors.add(new EventErrorEntity(eventRequest, packageId, String.format("%s: %s", TimeLineErrorCode.DATA_ERROR.getCode(),
                            TimeLineErrorCode.DATA_ERROR.getMessage()), channelCode));
                }
            }
            if (!CollectionUtils.isEmpty(listEvent)) {
                this.saveListEventManagementEntity(listEvent);
            }
            if (!CollectionUtils.isEmpty(eventErrors)) {
                eventErrorRepository.saveAll(eventErrors);
            }
        }
        log.info("[handleEvent] END requestId : {}", requestId);
        return new ResponseDto(ErrorConstant.SUCCESS,null, requestId, BaseConfigConstant.RESPONSE_STATUS_SUCCESS, eventErrors, eventErrors.size());
    }

    @Override
    public ResponseDto getEventType(String requestId) {
        log.info("[getEventType] requestId: {}", requestId);
        return new ResponseDto(ErrorConstant.SUCCESS,null, requestId, BaseConfigConstant.RESPONSE_STATUS_SUCCESS, eventTypeConfigRepository.findByIsActive(BaseConfigConstant.ACTIVE), 0);
    }

    @Override
    public ResponseDto getChannelCode(String requestId) {
        log.info("[getEventType] requestId: {}", requestId);
        return new ResponseDto(ErrorConstant.SUCCESS,null, requestId, BaseConfigConstant.RESPONSE_STATUS_SUCCESS, channelCodeRepo.findAll(), 0);
    }

    private EventManagementEntity doExecuteEventNew(Long startDate, Long now, BaseEvent eve, String packageId, String channelCode, String requestId) {
        EventManagementEntity entity = new EventManagementEntity(eve, packageId, channelCode);
        if (startDate <= now) {
            entity = this.saveEventManagementEntity(entity);
            sendKafkaTopic(entity, requestId);
//            entity.setStatus(BaseConfigConstant.StatusEnum.PROCESS.getValue());
        }
        return entity;
    }

    private EventManagementEntity doExecuteEventOld(Long startDate, Long now, BaseEvent eventRequest, EventManagementEntity eventManagementOld, String requestId)
            throws ServiceException {
        if (!eventManagementOld.getStatus().equals(BaseConfigConstant.StatusEnum.WAITING.getValue())) {
            throw new ServiceException(TimeLineErrorCode.STATUS_INVALID);
        }

        EventManagementEntity eveNew = new EventManagementEntity();
        BeanUtils.copyProperties(eventManagementOld, eveNew);

        if (now >= eveNew.getTimeStart()) {
            throw new ServiceException(TimeLineErrorCode.START_DATE_INVALID);
        }

        BeanUtils.copyProperties(eventRequest, eveNew);
        eveNew.setStatus(BaseConfigConstant.StatusEnum.WAITING.getValue());

        if(startDate <= now){
            sendKafkaTopic(eveNew, requestId);
//            eveNew.setStatus(BaseConfigConstant.StatusEnum.PROCESS.getValue());
        }

        return eveNew;
    }

    private void handlePackageIdExist(String packId) {
    	log.info("[handlePackageIdExist] START packageId: {}", packId);
    	List<EventManagementEntity> listEvents = eventManagementRepository.findByPackageIdAndStatusAndIsActive(packId,
                BaseConfigConstant.StatusEnum.WAITING.getValue(), BaseConfigConstant.ACTIVE);
    	if (!CollectionUtils.isEmpty(listEvents)) {
    		for (EventManagementEntity e : listEvents) {
    			e.setIsActive(BaseConfigConstant.IN_ACTIVE);
    		}
    	}
    	this.saveListEventManagementEntity(listEvents);
    	log.info("[handlePackageIdExist] END packageId: {}", packId);
    }
    
    private void validateEvent(BaseEvent eve, Long startDate, Long endDate, Long now, String channelCode) throws Exception {
        if (ObjectUtils.isEmpty(eve.getEventType())) {
            throw new ServiceException(TimeLineErrorCode.EVENT_TYPE_INVALID);
        }
        if(!Objects.nonNull(eventTypeConfigRepository.findByChannelCodeAndEventTypeCodeAndIsActive(channelCode,
                eve.getEventType(), BaseConfigConstant.ACTIVE))){
            throw new ServiceException(TimeLineErrorCode.EVENT_TYPE_NOT_FOUND);
        }
        List<ValidateResultConstant> validErrs = validationEventService.validateCommonByEventType(BaseConfigConstant.TypeGroupEnum.EVENT_TYPE.getValue(),
                eve.getEventType(), eve);
        if(!CollectionUtils.isEmpty(validErrs)){
            String errorMessage = validErrs.stream()
                    .map(ValidateResultConstant::getMessage)
                    .collect(Collectors.joining("; "));

            throw new ServiceException(TimeLineErrorCode.DATA_ERROR.getCode(), errorMessage);
        }
		if (Objects.nonNull(endDate) && startDate > endDate) {
			throw new ServiceException(TimeLineErrorCode.START_DATE_GT_END_DATE);
		}
		if (Objects.nonNull(endDate) && endDate < now) {
			throw new ServiceException(TimeLineErrorCode.END_DATE_GT_NOW);
		}

    }

    @Override
    @LogsActivityAnnotation
    public ResponseDto cancelEvent(EventManagementRequestDto requestDto, String requestId) throws ServiceException {
        log.info("[cancelEvent] START requestId : {}", requestId);
        List<Criteria> criteria ;
        List<EventManagementEntity> listEvent = new ArrayList<>();
        Query query = new Query();
        List<String> list = Collections.singletonList(BaseConfigConstant.StatusEnum.WAITING.getValue());
        criteria = createQueryList(requestDto,list);
        query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[criteria.size()])));
        int count = Math.toIntExact(mongoTemplate.count(query, EventManagementEntity.class));
        int page = count%BaseConfigConstant.PAGE_SIZE_EXECUTED > 0 ? count/BaseConfigConstant.PAGE_SIZE_EXECUTED + 1 : count/BaseConfigConstant.PAGE_SIZE_EXECUTED;

        log.info("requestId {}, count: {}, Page : {}", requestId, count, page);
        for (int i = 0; i < page; i ++){
            query.with(PageRequest.of(i, BaseConfigConstant.PAGE_SIZE_EXECUTED));
            List<EventManagementEntity> eventManagementEntities = mongoTemplate.find(query, EventManagementEntity.class);
            for(EventManagementEntity entity : eventManagementEntities) {
                entity.setStatus(BaseConfigConstant.StatusEnum.CANCEL.getValue());
                listEvent.add(entity);
            }
            this.saveListEventManagementEntity(listEvent);
            listEvent.clear();
        }
        log.info("[cancelEvent] END requestId : {}", requestId);
        return new ResponseDto(ErrorConstant.SUCCESS,null, requestId, BaseConfigConstant.RESPONSE_STATUS_SUCCESS, count);
    }

    public List<Criteria> createQueryList(EventManagementRequestDto requestDto, List<String> status){
        List<Criteria> listCriteria = new ArrayList<>();
        listCriteria.add(Criteria.where("status").in(status));
        listCriteria.add(Criteria.where("is_active").is(BaseConfigConstant.ACTIVE));
        if(!CollectionUtils.isEmpty(requestDto.getId()))
            listCriteria.add(Criteria.where("_id").in(requestDto.getId()));
        if(!CollectionUtils.isEmpty(requestDto.getEventType()))
            listCriteria.add(Criteria.where("event_type").in(requestDto.getEventType()));
        if(requestDto.getChannelCode() != null)
            listCriteria.add(Criteria.where("channel_code").is(requestDto.getChannelCode()));
        if(!CollectionUtils.isEmpty(requestDto.getUserId()))
            listCriteria.add(Criteria.where("user_id").in(requestDto.getUserId()));
        if(!CollectionUtils.isEmpty(requestDto.getProductCode()))
            listCriteria.add(Criteria.where("product_code").in(requestDto.getProductCode()));
        if(!CollectionUtils.isEmpty(requestDto.getContractCode()))
            listCriteria.add(Criteria.where("contract_code").in(requestDto.getContractCode()));
        if(!CollectionUtils.isEmpty(requestDto.getReferenceId()))
            listCriteria.add(Criteria.where("reference_id").in(requestDto.getReferenceId()));
        return listCriteria;
    }

    @Override
    public ResponseDto getJobMonitor(FindEventRequestDto findEventRequestDto) throws ServiceException {
        ResponseDto response = new ResponseDto(findEventRequestDto.getRequestId());

        log.info("[getJobMonitor] requestId: {}", findEventRequestDto.getRequestId());
        Long timeStart = DateUtils.convertStringDateToLong(findEventRequestDto.getFromDate(), DateUtils.FORMAT_DDMMYYYY_T_HHMMSS_Z);
        Long timeEnd = DateUtils.convertStringDateToLong(findEventRequestDto.getToDate(), DateUtils.FORMAT_DDMMYYYY_T_HHMMSS_Z);
        if(timeEnd <  timeStart){
            throw new ServiceException(TimeLineErrorCode.FROM_DATE_LT_TO_DATE);
        }
        Pageable pageable = PageRequest.of(findEventRequestDto.getPageIndex(), findEventRequestDto.getPageSize());
        PageCustom<JobMonitorEntity> data = eventManagementRepositoryCustom.getJob(timeStart, timeEnd, findEventRequestDto.getStatus(), findEventRequestDto.getId(), findEventRequestDto.getCountEvent(), pageable);
        log.info("[getJobMonitor] requestId: {} END", findEventRequestDto.getRequestId());
        return responseUtils.setResponseSuccess(response, data);
    }

    @Override
    public void handleEventRepeat(EventManagementEntity event) {
        if(event.getFrequencyRepetition()!=null && event.getFrequencyRepetition()>0){
            EventManagementEntity eventNew = new EventManagementEntity();
            BeanUtils.copyProperties(event, eventNew);

            eventNew.setId(null);
            eventNew.setVersion(null);
            eventNew.setStatus(BaseConfigConstant.StatusEnum.WAITING.getValue());
            eventNew.setTimeStart(event.getTimeStart() + TimeUnit.DAYS.toMillis(event.getFrequencyRepetition()));
            eventNew.setTimeEnd(event.getTimeEnd() + TimeUnit.DAYS.toMillis(event.getFrequencyRepetition()));

            this.saveEventManagementEntity(eventNew);
        }

    }

    @Override
    public void handleEventError(BaseEventDto baseEventDto, TimeLineErrorCode timeLineErrorCode) {
        List<BaseEvent> events = baseEventDto.getEvents();
        List<EventErrorEntity> eventErrors = events.stream()
                .map(e -> new EventErrorEntity(e, baseEventDto.getPackageId(), timeLineErrorCode.getMessage(), baseEventDto.getChannelCode()))
                .collect(Collectors.toList());
        eventErrorService.saveEventErrors(eventErrors);
    }

    public void sendKafkaTopic(EventManagementEntity eventManagementEntities, String requestId){
        KafkaRequestDto<Object> requestDto = new KafkaRequestDto<Object>();
        requestDto.setRequestId(requestId!=null ? requestId : UUID.randomUUID().toString());
        String topic = null;

        if(BaseConfigConstant.ChannelCodeEnum.BOND_SERVICE.getValue().equals(eventManagementEntities.getChannelCode())){
            EvenTriggerBondRequestDto evenTriggerBondRequestDto = new EvenTriggerBondRequestDto(eventManagementEntities);
            requestDto.setPayload(evenTriggerBondRequestDto);

            topic = topicEventBond;
        }else if(BaseConfigConstant.ChannelCodeEnum.COPYTRADE_SERVICE.getValue().equals(eventManagementEntities.getChannelCode())){
            EvenTriggerCopyTradeRequestDto evenTriggerCopyTradeRequestDto = new EvenTriggerCopyTradeRequestDto(eventManagementEntities);
            requestDto.setPayload(evenTriggerCopyTradeRequestDto);

            topic = topicEventCopytrade;
        }else if(BaseConfigConstant.ChannelCodeEnum.FUND_SERVICE.getValue().equals(eventManagementEntities.getChannelCode())){
            EvenTriggerFundRequestDto evenTriggerFundRequestDto = new EvenTriggerFundRequestDto(eventManagementEntities);
            requestDto.setPayload(evenTriggerFundRequestDto);

            topic = topicEventFund;
        }

        if(topic!=null){
            kafkaProduceService.sendEventKafka(requestDto, topic, null);
            handleEventRepeat(eventManagementEntities);
        }
    }

    @Override
    public ResponseDto executedEvents(EventManagementRequestDto request, String requestId) {
        List<EventErrorEntity> eventErrors = new ArrayList<>();
        Query query = new Query();
        List<EventManagementEntity> saveList = new ArrayList<>();
        List<Criteria> criteria ;
        int count = 0;
        try {
            List<String> list = Arrays.asList(
                    BaseConfigConstant.StatusEnum.WAITING.getValue(),
                    BaseConfigConstant.StatusEnum.FAILED_BY_SENDING.getValue(),
                    BaseConfigConstant.StatusEnum.FAILED_BY_EXECUTING.getValue(),
                    BaseConfigConstant.StatusEnum.CANCEL.getValue());
            criteria = createQueryList(request, list);
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[criteria.size()])));
            count = Math.toIntExact(mongoTemplate.count(query, EventManagementEntity.class));
            int page = count%BaseConfigConstant.PAGE_SIZE_EXECUTED > 0 ? count/BaseConfigConstant.PAGE_SIZE_EXECUTED + 1
                    : count/BaseConfigConstant.PAGE_SIZE_EXECUTED;

            log.info("requestId {}, Page {} ", requestId, page);
            for (int i = 0; i < page; i ++){
                query.with(PageRequest.of(i, BaseConfigConstant.PAGE_SIZE_EXECUTED));
                List<EventManagementEntity> eventManagementEntities = mongoTemplate.find(query, EventManagementEntity.class);

                for(EventManagementEntity event : eventManagementEntities){
                    sendKafkaTopic(event, requestId);
//                    event.setStatus(BaseConfigConstant.StatusEnum.PROCESS.getValue());
//                    saveList.add(event);
                }
//                this.saveListEventManagementEntity(saveList);
            }

        } catch (Exception e) {
            log.error("[executedEvents] exception requestId: {} ERROR OUT: {}", requestId, e.getMessage());
        }
        if (!CollectionUtils.isEmpty(eventErrors)) {
            eventErrorRepository.saveAll(eventErrors);
        }
        log.info("[sendEvent] END)");
        return new ResponseDto(ErrorConstant.SUCCESS,null, requestId, BaseConfigConstant.RESPONSE_STATUS_SUCCESS, null);
    }

}
