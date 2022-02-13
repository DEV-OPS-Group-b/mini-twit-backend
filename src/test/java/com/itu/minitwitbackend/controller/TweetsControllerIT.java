package com.itu.minitwitbackend.controller;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itu.minitwitbackend.repository.TweetRepository;
import com.itu.minitwitbackend.repository.entity.TweetEntity;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TweetsControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private TweetRepository tweetRepository;

    @BeforeEach
    void beforeEach() {
        tweetRepository.deleteAll();
        tweetRepository.save(TweetEntity.builder()
                .username("me")
                .insertionDate(LocalDateTime.now())
                .tweet("my first tweet, hello there")
                .build());

        tweetRepository.save(TweetEntity.builder()
                .username("me")
                .insertionDate(LocalDateTime.now())
                .tweet("my second tweet, hello there again")
                .build());

        tweetRepository.save(TweetEntity.builder()
                .username("you")
                .insertionDate(LocalDateTime.now())
                .tweet("my first tweet, hello there")
                .build());
    }

    @Test
    void get_User_Tweets_Ok() {
        // arrange
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        // act
        ResponseEntity<Object[]> responseEntity = testRestTemplate.getForEntity("/devops/tweet/getAllUserTweets/me",Object[].class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().length).isEqualTo(2);

    }

    @Test
    void get_All_Tweets_Ok() {
        // arrange
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        // act
        ResponseEntity<Object[]> responseEntity = testRestTemplate.getForEntity("/devops/tweet/getAllTweets",Object[].class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().length).isEqualTo(3);

    }

    @Test
    void post_Tweets_Ok() {
        // arrange
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        // act
        ResponseEntity<TweetEntity> responseEntity = testRestTemplate.postForEntity("/devops/tweet/addTweet"
                ,TweetEntity.builder()
                        .username("me")
                        .insertionDate(LocalDateTime.now())
                        .tweet("my second tweet, hello there again")
                        .build()
                ,TweetEntity.class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(tweetRepository.findAll().size()).isEqualTo(4);

    }

}
