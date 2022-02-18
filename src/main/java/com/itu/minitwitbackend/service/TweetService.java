package com.itu.minitwitbackend.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import com.itu.minitwitbackend.controller.api.model.TweetFlagRequest;
import com.itu.minitwitbackend.exception.TweetNotFoundException;
import com.itu.minitwitbackend.exception.UnauthorizedException;
import com.itu.minitwitbackend.exception.UserNotFoundException;
import com.itu.minitwitbackend.repository.TweetRepository;
import com.itu.minitwitbackend.repository.UserRepository;
import com.itu.minitwitbackend.repository.entity.TweetEntity;
import com.itu.minitwitbackend.repository.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TweetService {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final TweetRepository tweetRepository;

    private final UserRepository userRepository;

    public TweetService(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    public List<TweetEntity> findByUsername(String username) {
        return tweetRepository.findByUsername(username);
    }

    public List<TweetEntity> getAllTweetsSorted() {
        var sortedTweets = tweetRepository.findAll();
        Collections.sort(sortedTweets, Comparator.comparing(TweetEntity::getInsertionDate).reversed());
        return sortedTweets;
    }

    public TweetEntity saveTweet(TweetEntity tweet) {
        LocalDateTime now = LocalDateTime.now();
        tweet.setInsertionDate(now.format(DATE_TIME_FORMATTER));
        return tweetRepository.save(tweet);
    }

    public void flagTweet(TweetFlagRequest tweetFlagRequest) {
        var user = userRepository.findByUsernameAndPassword(
                tweetFlagRequest.getUsername(), tweetFlagRequest.getPassword());
        validateUserPermission(user);
        updateTweetWithFlag(tweetFlagRequest);

    }

    private void updateTweetWithFlag(TweetFlagRequest tweetFlagRequest) {

        tweetRepository.findById(tweetFlagRequest.getTweetId()).ifPresentOrElse(tweet -> {
                    log.info("tweet {} is flagged updated successfully ", tweetFlagRequest.getTweetId());
                    tweet.setFlagged(true);
                    tweetRepository.save(tweet);
                }
                , () -> {
                    log.error("tweet {} has not been flagged since it is not found", tweetFlagRequest.getTweetId());
                    throw new TweetNotFoundException("tweet not found");
                });
    }


    private void validateUserPermission(Optional<UserEntity> user) {
        if (user.isPresent() && (user.get().getIsAdmin() == null || !user.get().getIsAdmin())) {
            throw new UnauthorizedException("this user does not have permission to flag this tweet");
        }
        if (user.isEmpty()) {
            throw new UserNotFoundException("we do not have such user");
        }
    }
}
