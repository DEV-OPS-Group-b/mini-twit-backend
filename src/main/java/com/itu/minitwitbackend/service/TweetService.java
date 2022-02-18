package com.itu.minitwitbackend.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itu.minitwitbackend.controller.api.model.TweetFlagRequest;
import com.itu.minitwitbackend.exception.InvalidCredentialsException;
import com.itu.minitwitbackend.exception.TweetNotFoundException;
import com.itu.minitwitbackend.repository.TweetRepository;
import com.itu.minitwitbackend.repository.entity.TweetEntity;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TweetService {
    private final TweetRepository repository;

    public TweetService(TweetRepository repository) {
        this.repository = repository;
    }

    public List<TweetEntity> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public List<TweetEntity> getAllTweetsSorted() {
        var sortedTweets = repository.findAll();
        Collections.sort(sortedTweets, Comparator.comparing(TweetEntity::getInsertionDate).reversed());
        return sortedTweets;
    }

    public TweetEntity saveTweet(TweetEntity tweet) {
        return repository.save(tweet);
    }

    public void flagTweet(TweetFlagRequest tweetFlagRequest) {
        repository.findById(tweetFlagRequest.getTweetId()).ifPresentOrElse(tweet -> {
                    log.info("tweet {} is flagged updated successfully ", tweetFlagRequest.getTweetId());
                    tweet.setFlagged(true);
                    repository.save(tweet);
                }
                , () -> {
                    log.error("tweet {} has not been flagged since it is not found", tweetFlagRequest.getTweetId());
                    throw new TweetNotFoundException("tweet not found");
                });

    }
}
