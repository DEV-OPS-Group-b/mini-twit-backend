package com.itu.minitwitbackend.repository.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("tweet")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TweetEntity {
    @Id
    private String id;
    @Indexed
    private String username;
    private LocalDateTime insertionDate;
    private String tweet;
}
