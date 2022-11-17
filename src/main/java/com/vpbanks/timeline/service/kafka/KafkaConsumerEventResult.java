package com.vpbanks.timeline.service.kafka;

import com.vpbanks.timeline.repository.entity.EventManagementEntity;
import com.vpbanks.timeline.request.EventResultRequestDto;
import com.vpbanks.timeline.request.KafkaRequestDto;
import com.vpbanks.timeline.service.EventManagementService;
import com.vpbanks.timeline.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;

@Service
@Slf4j
@AllArgsConstructor
public class KafkaConsumerEventResult {

    private final EventManagementService eventManagementService;

    @KafkaListener(topics = "${spring.kafka.consumer.timeline.result.topic}", groupId = "${spring.kafka.consumer.timeline.result.group-id}",
        containerFactory = "kafkaListenerEventResult")
    public void consumerEventResult(String message) {
        log.info("consumerDebtInfo message = {}", JsonUtil.toJsonString(message));
        EventResultRequestDto request = null;
        try {
            KafkaRequestDto messageEventResultRequestDto = JsonUtil.getGenericObject(message, KafkaRequestDto.class);
            request = JsonUtil.getGenericObject(messageEventResultRequestDto.getPayload(), EventResultRequestDto.class);
            eventManagementService.consumerEventResult(request);
        } catch (Exception ex) {
            log.error("consumerDebtInfo error = {} - {}", ex, ex.getMessage());
            ex.printStackTrace();
        }
    }
}
