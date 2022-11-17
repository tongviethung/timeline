package com.vpbanks.timeline.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "channel_code")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChannelCodeEntity {

    @Id
    private String id;

    @Field(value = "name")
    private String name;
}
