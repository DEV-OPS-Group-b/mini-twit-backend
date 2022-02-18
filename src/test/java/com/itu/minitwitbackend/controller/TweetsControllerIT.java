package com.itu.minitwitbackend.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itu.minitwitbackend.controller.api.model.TweetFlagRequest;
import com.itu.minitwitbackend.repository.TweetRepository;
import com.itu.minitwitbackend.repository.UserRepository;
import com.itu.minitwitbackend.repository.entity.TweetEntity;
import com.itu.minitwitbackend.repository.entity.UserEntity;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TweetsControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private TweetRepository tweetRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        tweetRepository.deleteAll();
        userRepository.deleteAll();
        tweetRepository.save(TweetEntity.builder()
                .username("me")
                .insertionDate(LocalDateTime.now())
                .tweet("my first tweet, hello there")
                .flagged(false)
                .build());

        tweetRepository.save(TweetEntity.builder()
                .username("me")
                .insertionDate(LocalDateTime.of(2000, 12, 2, 10, 05))
                .tweet("my second tweet, hello there again")
                .flagged(false)
                .build());

        tweetRepository.save(TweetEntity.builder()
                .username("you")
                .insertionDate(LocalDateTime.of(1991, 12, 2, 10, 05))
                .tweet("my last tweet, hello there")
                .flagged(false)
                .build());
    }

    @Test
    void get_User_Tweets_Ok() {
        // arrange
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        // act
        ResponseEntity<Object[]> responseEntity = testRestTemplate.getForEntity("/devops/tweet/getAllUserTweets/me", Object[].class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().length).isEqualTo(2);

    }

    @Test
    void get_All_Tweets_Ok() throws NoSuchFieldException {
        // arrange
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        // act
        ResponseEntity<Object[]> responseEntity = testRestTemplate.getForEntity("/devops/tweet/getAllTweets", Object[].class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().length).isEqualTo(3);
        var firstTweet = Arrays.stream(responseEntity.getBody()).findFirst().get().toString();
        assertThat(firstTweet.contains("my first tweet, hello there")).isTrue();

    }

    @Test
    void post_Tweets_Ok() {
        // arrange
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        // act
        ResponseEntity<TweetEntity> responseEntity = testRestTemplate.postForEntity("/devops/tweet/addTweet"
                , TweetEntity.builder()
                        .username("me")
                        .insertionDate(LocalDateTime.now())
                        .tweet("my second tweet, hello there again")
                        .build()
                , TweetEntity.class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(tweetRepository.findAll().size()).isEqualTo(4);

    }

    @Test
    void flag_Tweets_Success() {
        // arrange
        var tweet = tweetRepository.save(TweetEntity.builder()
                .username("random")
                .insertionDate(LocalDateTime.of(2002, 12, 2, 10, 05))
                .tweet("random tweet")
                .flagged(false)
                .build());
        var admin = userRepository.save(UserEntity.builder()
                .username("admin")
                .password("admin")
                .isAdmin(true)
                .build());

        // act
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/devops/tweet/update-Tweet-flag"
                , TweetFlagRequest.builder()
                        .username("admin")
                        .password("admin")
                        .tweetId(tweet.getId())
                        .flag(true)
                        .build()
                , String.class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(tweetRepository.findById(tweet.getId()).get().isFlagged()).isTrue();

    }

    @Test
    void flag_Tweets_NotFound() {
        // arrange
        // arrange
        var tweet = tweetRepository.save(TweetEntity.builder()
                .username("random")
                .insertionDate(LocalDateTime.of(2002, 12, 2, 10, 05))
                .tweet("random tweet")
                .flagged(false)
                .build());
        var admin = userRepository.save(UserEntity.builder()
                .username("admin")
                .password("admin")
                .isAdmin(true)
                .build());
        // act
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/devops/tweet/update-Tweet-flag"
                , TweetFlagRequest.builder()
                        .tweetId("123")
                        .flag(true)
                        .username("admin")
                        .password("admin")
                        .build()
                , String.class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }


    @Test
    void flag_Tweets_Unauthorized_Not_Admin() {
        // arrange
        var tweet = tweetRepository.save(TweetEntity.builder()
                .username("random")
                .insertionDate(LocalDateTime.of(2002, 12, 2, 10, 05))
                .tweet("random tweet")
                .flagged(false)
                .build());
        var notAdmin = userRepository.save(UserEntity.builder()
                .username("not-admin")
                .password("admin")
                .isAdmin(false)
                .build());
        // act
        ResponseEntity<String> responseEntity = testRestTemplate.exchange("/devops/tweet/update-Tweet-flag",
                HttpMethod.POST,
                new HttpEntity<>(TweetFlagRequest.builder()
                        .tweetId(tweet.getId())
                        .username(notAdmin.getUsername())
                        .password("admin")
                        .flag(true)
                        .build())

                , String.class);


        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }


    @Test
    void flag_Tweets_Unauthorized_Property_Not_Set() {
        // arrange
        var tweet = tweetRepository.save(TweetEntity.builder()
                .username("random")
                .insertionDate(LocalDateTime.of(2002, 12, 2, 10, 05))
                .tweet("random tweet")
                .flagged(false)
                .build());
        var admin = userRepository.save(UserEntity.builder()
                .username("admin")
                .password("admin")
                .build());
        // act
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/devops/tweet/update-Tweet-flag"
                , TweetFlagRequest.builder()
                        .tweetId("123")
                        .flag(true)
                        .username("admin")
                        .password("admin")
                        .build()
                , String.class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }

}
