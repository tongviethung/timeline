package com.vpbanks.timeline.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.retry.annotation.EnableRetry;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableRetry
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;
    
    @Value("${spring.kafka.consumer.timeline.event.group-id}")
    private String eventInfoGroupId;

    @Value("${spring.kafka.consumer.timeline.result.group-id}")
    private String eventResultGroupId;

    public ConsumerFactory<String, String> consumerFactory(Map<String, Object> props) {
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    //================group-id: timeline.service.group.event.info.dev====================//
    @Bean
    public ConsumerFactory<String, String> eventInfoConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, eventInfoGroupId);

        return consumerFactory(props);
    }

    @Bean(name = "kafkaListenerEvent")
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerEvent() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(eventInfoConsumerFactory());
        return factory;
    }
    //================group-id: timeline.service.group.event.info.dev====================//

    //================group-id: timeline.service.group.result.info.dev====================//
    @Bean
    public ConsumerFactory<String, String> eventResultConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, eventResultGroupId);

        return consumerFactory(props);
    }

    @Bean(name = "kafkaListenerEventResult")
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerEventResult() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(eventResultConsumerFactory());
        return factory;
    }
    //================group-id: timeline.service.group.result.info.dev====================//

}
