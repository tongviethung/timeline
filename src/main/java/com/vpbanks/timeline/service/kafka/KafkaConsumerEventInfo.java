package com.vpbanks.timeline.service.kafka;

import com.vpbanks.timeline.config.redis.RedisCache;
import com.vpbanks.timeline.constants.BaseConfigConstant;
import com.vpbanks.timeline.request.BaseEvent;
import com.vpbanks.timeline.request.BaseEventDto;
import com.vpbanks.timeline.request.KafkaRequestDto;
import com.vpbanks.timeline.response.exception.ServiceException;
import com.vpbanks.timeline.service.EventManagementService;
import com.vpbanks.timeline.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.vpbanks.timeline.constants.ErrorConstant.TimeLineErrorCode;

@Component
@AllArgsConstructor
@Slf4j
public class KafkaConsumerEventInfo {

	private final EventManagementService eventManagementService;
	private final RedisCache redisCache;

	@KafkaListener(topics = "${spring.kafka.consumer.timeline.event.topic}", groupId = "${spring.kafka.consumer.timeline.event.group-id}",
		containerFactory = "kafkaListenerEvent")
	public void consumerKafkaEventInfo(String message) {
		log.info("[consumerKafkaEventInfo] message: {}", message);
		KafkaRequestDto kafkaRequestDto;
		BaseEventDto baseEventDto;
		try {
			kafkaRequestDto = JsonUtil.getGenericObject(message, KafkaRequestDto.class);
			baseEventDto = JsonUtil.getGenericObject(kafkaRequestDto.getPayload(), BaseEventDto.class);
		} catch (IOException e) {
			log.error("[consumerKafkaEventInfo] ERROR OUT: {}", e.getMessage());
			return;
		}

		baseEventDto.setRequestId(kafkaRequestDto.getRequestId()!=null ? kafkaRequestDto.getRequestId() : UUID.randomUUID().toString());
		boolean checkDup = isDuplicateMessage(baseEventDto);
		if(checkDup){
			eventManagementService.handleEventError(baseEventDto, TimeLineErrorCode.DUPLICATE_EVENT);
			return;
		}

		if(ObjectUtils.isEmpty(baseEventDto.getChannelCode())){
			eventManagementService.handleEventError(baseEventDto, TimeLineErrorCode.CHANNEL_CODE_IS_REQUIRED);
			log.info("[consumerKafkaEventInfo] done: channel_code is null");
		}else{
			if(baseEventDto.getChannelCode().equals(BaseConfigConstant.ChannelCodeEnum.BOND_SERVICE.getValue())){
				if (ObjectUtils.isEmpty(baseEventDto.getPackageId())) {
					log.error("[consumerKafka] ERROR: packageId is null");
					eventManagementService.handleEventError(baseEventDto, TimeLineErrorCode.PACKAGE_ID_REQUIRED);
					return;
				}
			}

			List<BaseEvent> events = baseEventDto.getEvents();
			try {
				eventManagementService.handleBaseEvent(events, baseEventDto.getPackageId(), baseEventDto.getRequestId(), baseEventDto.getChannelCode());
			} catch (ServiceException e) {
				log.error("[consumerKafka] ERROR OUT: {}", e.getMessage());
			}
		}
	}

	private boolean isDuplicateMessage(BaseEventDto baseEventDto) {
		String key = baseEventDto.getChannelCode() + baseEventDto.getRequestId();
		Set<Object> set = redisCache.memberOpsForSet(key);
		if(CollectionUtils.isEmpty(set)){
			redisCache.addKeyOpsForSetWithTimeLive(key, "", 30, TimeUnit.SECONDS);
			return false;
		}
		return true;
	}
}
