package com.itu.minitwitbackend.controller;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.itu.minitwitbackend.controller.api.model.FollowUserRequest;
import com.itu.minitwitbackend.repository.UserRepository;
import com.itu.minitwitbackend.repository.entity.UserEntity;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUser = UserEntity.builder()
            .username("she")
            .password("pas")
            .email("mail@mail.com")
            .profilePicture("no pic")
            .build();

    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();
    }

    @Test
    void get_User_Ok() {
        // arrange
       var useId = userRepository.save(testUser).getId();
        // act
        ResponseEntity<UserEntity> responseEntity = testRestTemplate.getForEntity(
                "/devops/user/get-user/she",  UserEntity.class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getUsername()).isEqualTo("she");
        assertThat(responseEntity.getBody().getId()).isEqualTo(useId);
        assertThat(responseEntity.getBody().getEmail()).isEqualTo("mail@mail.com");
        assertThat(responseEntity.getBody().getPassword()).isEqualTo("pas");

    }

    @Test
    void get_User_Not_Found() {
        // arrange
        // act
        ResponseEntity<UserEntity> responseEntity = testRestTemplate.getForEntity(
                "/devops/user/get-user/INVALID",  UserEntity.class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void create_User_Created() {
        // arrange
        // act
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "/devops/user/register", testUser, String.class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void create_User_BadRequest() {
        // arrange
        // act
        var user = new UserEntity();
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "/devops/user/register", user, String.class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void create_User_AlreadyExists() {
        // arrange
        userRepository.save(testUser).getId();
        // act
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "/devops/user/register", testUser, String.class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

    }

    @Test
    void login_OK() {
        // arrange
        userRepository.save(testUser).getId();
        // act
        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(
                "/devops/user/login/she/pas", String.class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FOUND);

    }
    @Test
    void login_UNAUTHORIZED() {
        // arrange
        // act
        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(
                "/devops/user/login/MOCK/MOCKY", String.class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }

    @Test
    void follow_User_Success() {
        // arrange
        var savedUser = userRepository.save(testUser);
        var user = new FollowUserRequest(testUser.getUsername(),"he");

        // act
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "/devops/user/follow", user, String.class);

        var user2 = userRepository.findById(savedUser.getId());

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(user2.get().getFollowing().size()).isEqualTo(1);
    }

    @Test
    void unfollow_User_Success() {
        // arrange
        testUser.setFollowing(new ArrayList<>(){{add("me");}});
        var savedUser = userRepository.save(testUser);
        var user = new FollowUserRequest(testUser.getUsername(),"me");

        // act
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "/devops/user/unfollow", user, String.class);

        var user2 = userRepository.findById(savedUser.getId());

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(user2.get().getFollowing().size()).isEqualTo(0);
    }

    @Test
    void isFollowing_True_Success() {
        // arrange
        testUser.setFollowing(new ArrayList<>(){{add("me");}});
         userRepository.save(testUser);
        var user = new FollowUserRequest(testUser.getUsername(),"me");

        // act
        ResponseEntity<Boolean> responseEntity = testRestTemplate.postForEntity(
                "/devops/user/isFollowing", user, Boolean.class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().booleanValue()).isEqualTo(true);
    }

    @Test
    void isFollowing_False_Success() {
        // arrange
         userRepository.save(testUser);
        var user = new FollowUserRequest(testUser.getUsername(),"me");

        // act
        ResponseEntity<Boolean> responseEntity = testRestTemplate.postForEntity(
                "/devops/user/isFollowing", user, Boolean.class);

        // assert
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().booleanValue()).isEqualTo(false);
    }

    @Test
    void follow_validate_isFollowing_Success() {
        // arrange
        var savedUser = userRepository.save(testUser);
        var firstUser = new FollowUserRequest(testUser.getUsername(),"he");
        var secondUser = new FollowUserRequest(testUser.getUsername(),"you");


        // act
        ResponseEntity<String> responseEntity0 = testRestTemplate.postForEntity(
                "/devops/user/follow", firstUser, String.class);

        // act
        ResponseEntity<String> responseEntity1 = testRestTemplate.postForEntity(
                "/devops/user/follow", secondUser, String.class);

        var userFromDb = userRepository.findById(savedUser.getId());

        // assert
        assertThat(responseEntity0.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userFromDb.get().getFollowing().size()).isEqualTo(2);
       // ------- is following setup-------

        // act
        ResponseEntity<Boolean> responseEntity2 = testRestTemplate.postForEntity(
                "/devops/user/isFollowing", firstUser, Boolean.class);

        ResponseEntity<Boolean> responseEntity3 = testRestTemplate.postForEntity(
                "/devops/user/isFollowing", secondUser, Boolean.class);

        // assert
        assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity3.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity2.getBody().booleanValue()).isEqualTo(true);
        assertThat(responseEntity3.getBody().booleanValue()).isEqualTo(true);
    }
}
