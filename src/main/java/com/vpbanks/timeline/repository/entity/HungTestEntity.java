package com.vpbanks.timeline.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "hungtest")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HungTestEntity {

    @Id
    private String id;

    @Field(value = "value")
    private int value;
}
