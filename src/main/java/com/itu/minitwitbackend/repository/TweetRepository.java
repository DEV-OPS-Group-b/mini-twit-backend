package com.itu.minitwitbackend.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.itu.minitwitbackend.repository.entity.TweetEntity;

public interface TweetRepository extends MongoRepository<TweetEntity, String> {
    List<TweetEntity> findByUsername(String username);
}
