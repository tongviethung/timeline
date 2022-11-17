package com.vpbanks.timeline.repository.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "event_type_config")
public class EventTypeConfigEntity {
    @Id
    private String id;

    @Field(value = "event_type_code")
    private String eventTypeCode;

    @Field(value = "event_type_name")
    private String eventTypeName;

    @Field(value = "channel_code")
    private String channelCode;

    @Field(value = "is_active")
    private int isActive;

    @Field(value = "description")
    private String description;
}
