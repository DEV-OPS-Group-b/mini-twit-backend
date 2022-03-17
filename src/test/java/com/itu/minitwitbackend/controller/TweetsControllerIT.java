package com.itu.minitwitbackend.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
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

import static com.itu.minitwitbackend.service.TweetService.DATE_TIME_FORMATTER;
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
                .insertionDate(LocalDateTime.of(2000, 12, 2, 10, 5).format(DATE_TIME_FORMATTER))
                .tweet("my second tweet, hello there again")
                .flagged(false)
                .build());

        tweetRepository.save(TweetEntity.builder()
                .username("me")
                .insertionDate(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .tweet("my first tweet, hello there")
                .flagged(false)
                .build());

        tweetRepository.save(TweetEntity.builder()
                .username("you")
                .insertionDate(LocalDateTime.of(1991, 12, 2, 10, 5).format(DATE_TIME_FORMATTER))
                .tweet("my last tweet, hello there")
                .flagged(false)
                .build());

        for (int i = 0; i < 300; i++) {

            tweetRepository.save(TweetEntity.builder()
                    .username("me")
                    .insertionDate(LocalDateTime.of(2000, 12, 2, 10, 5).format(DATE_TIME_FORMATTER))
                    .tweet("my" + i + "tweet, hello there again")
                    .flagged(false)
                    .build());
        }
        var tweetsCounter = tweetRepository.findAll().size();
    }

    @Test
    void get_User_Tweets_Ok() {
        // arrange
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        // act
        ResponseEntity<Object[]> responseEntity = testRestTemplate.getForEntity("/devops/tweet/get-user-tweets/me", Object[].class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).length).isEqualTo(2);

    }

    @Test
    void get_All_Tweets_Ok() {
        // arrange
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        // act
        ResponseEntity<Object[]> responseEntity = testRestTemplate.getForEntity("/devops/tweet/get-all-tweets/200/0", Object[].class);
        ResponseEntity<Object[]> responseEntity2 = testRestTemplate.getForEntity("/devops/tweet/get-all-tweets/100/1", Object[].class);
        ResponseEntity<Object[]> responseEntity3 = testRestTemplate.getForEntity("/devops/tweet/get-all-tweets/400/0", Object[].class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).length).isEqualTo(200);
        assertThat(Objects.requireNonNull(responseEntity2.getBody()).length).isEqualTo(100);
        assertThat(Objects.requireNonNull(responseEntity3.getBody()).length).isEqualTo(303);
        var firstTweet = Arrays.stream(responseEntity.getBody()).findFirst().get().toString();
        assertThat(firstTweet.contains("my first tweet, hello there")).isTrue();

    }

    @Test
    void post_Tweets_Ok() {
        // arrange
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        // act
        ResponseEntity<TweetEntity> responseEntity = testRestTemplate.postForEntity("/devops/tweet/add-tweet"
                , TweetEntity.builder()
                        .username("me")
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
                .insertionDate(LocalDateTime.of(2002, 12, 2, 10, 5).format(DATE_TIME_FORMATTER))
                .tweet("random tweet")
                .flagged(false)
                .build());

        userRepository.save(UserEntity.builder()
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
        tweetRepository.save(TweetEntity.builder()
                .username("random")
                .tweet("random tweet")
                .flagged(false)
                .build());

        userRepository.save(UserEntity.builder()
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
                .insertionDate(LocalDateTime.of(2002, 12, 2, 10, 5).format(DATE_TIME_FORMATTER))
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
        tweetRepository.save(TweetEntity.builder()
                .username("random")
                .insertionDate(LocalDateTime.of(2002, 12, 2, 10, 5).format(DATE_TIME_FORMATTER))
                .tweet("random tweet")
                .flagged(false)
                .build());
        userRepository.save(UserEntity.builder()
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
