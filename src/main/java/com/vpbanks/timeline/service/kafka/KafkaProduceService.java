package com.vpbanks.timeline.service.kafka;

import com.vpbanks.timeline.constants.BaseConfigConstant;
import com.vpbanks.timeline.repository.EventManagementRepository;
import com.vpbanks.timeline.repository.JobMonitorRepository;
import com.vpbanks.timeline.repository.entity.EventManagementEntity;
import com.vpbanks.timeline.repository.entity.JobMonitorEntity;
import com.vpbanks.timeline.request.KafkaRequestDto;
import com.vpbanks.timeline.service.EventManagementService;
import com.vpbanks.timeline.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.concurrent.ListenableFuture;

@Service
@Slf4j
public class KafkaProduceService {

    @Qualifier("kafkaProducerScanEvent")
    private final KafkaTemplate<String, Object> kafkaTemplateScanEvent;
    private final JobMonitorRepository jobMonitorRepository;
    private final EventManagementRepository eventManagementRepository;
    private final EventManagementService eventManagementService;

    @Autowired
    public KafkaProduceService(KafkaTemplate<String, Object> kafkaTemplateScanEvent, JobMonitorRepository jobMonitorRepository,
                               @Lazy EventManagementService eventManagementService,
                               EventManagementRepository eventManagementRepository) {
        this.kafkaTemplateScanEvent = kafkaTemplateScanEvent;
        this.jobMonitorRepository = jobMonitorRepository;
        this.eventManagementService = eventManagementService;
        this.eventManagementRepository = eventManagementRepository;
    }

    public void sendEventKafka(KafkaRequestDto requestDto, String topic, String jobMonitorId) {
        ListenableFuture<SendResult<String, Object>> future =
                kafkaTemplateScanEvent.send(topic, requestDto);
        future.addCallback(new KafkaSendCallback<>() {
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                log.info("Sent message=[" + JsonUtil.toJsonString(requestDto) + "] with offset=["
                        + result.getRecordMetadata().offset() + "]");
                handleSuccessSending(requestDto, jobMonitorId);
            }

            @Override
            public void onFailure(KafkaProducerException ex) {
                log.info("Unable to send message=[" + JsonUtil.toJsonString(requestDto) + "] due to : " + ex.getMessage());
                if(!ObjectUtils.isEmpty(jobMonitorId)){
                    handleJobMonitorFailSending(jobMonitorId, requestDto);
                }
            }
        });
    }

    private void handleSuccessSending(KafkaRequestDto requestDto, String jobMonitorId) {
        EventManagementEntity eventManagementEntity = JsonUtil.getGenericObject(requestDto.getPayload(), EventManagementEntity.class);
        EventManagementEntity eventManagement = eventManagementRepository.findById(eventManagementEntity.getId()).orElse(null);
        if(!ObjectUtils.isEmpty(eventManagement)){
            eventManagement.setStatus(BaseConfigConstant.StatusEnum.PROCESS.getValue());

            eventManagementService.saveEventManagementEntity(eventManagement);
        }

        JobMonitorEntity jobMonitorEntity = jobMonitorRepository.findById(jobMonitorId).orElse(null);
        if(!ObjectUtils.isEmpty(jobMonitorEntity)){
            jobMonitorEntity.setTotalEventSuccess(jobMonitorEntity.getTotalEventSuccess() + 1);
            jobMonitorRepository.save(jobMonitorEntity);
        }
    }

    void handleJobMonitorFailSending(String jobMonitorId, KafkaRequestDto requestDto){
        EventManagementEntity eventManagementEntity = JsonUtil.getGenericObject(requestDto.getPayload(), EventManagementEntity.class);
        EventManagementEntity eventManagement = eventManagementRepository.findById(eventManagementEntity.getId()).orElse(null);
        if(!ObjectUtils.isEmpty(eventManagement)){
            eventManagement.setStatus(BaseConfigConstant.StatusEnum.FAILED_BY_SENDING.getValue());

            eventManagementService.saveEventManagementEntity(eventManagement);
        }

        JobMonitorEntity jobMonitorEntity = jobMonitorRepository.findById(jobMonitorId).orElse(null);
        if(!ObjectUtils.isEmpty(jobMonitorEntity)){
            jobMonitorEntity.setTotalEventError(jobMonitorEntity.getTotalEventError() + 1);
            jobMonitorEntity.setStatus(BaseConfigConstant.StatusEnum.FAILED.getValue());

            jobMonitorRepository.save(jobMonitorEntity);
        }
    }
}
