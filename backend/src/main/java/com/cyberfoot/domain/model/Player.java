package com.cyberfoot.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "players")
public record Player(
    @Id
    @Field("_id")
    String id,
    String clubId,
    String name,
    int rating
) {
    // ...existing code...
}
