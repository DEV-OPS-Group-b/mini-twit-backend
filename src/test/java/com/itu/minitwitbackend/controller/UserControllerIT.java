package com.itu.minitwitbackend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
                "/devops/user/getUser/she",  UserEntity.class);

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
                "/devops/user/getUser/INVALID",  UserEntity.class);

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
        var use = new UserEntity();
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                "/devops/user/register", use, String.class);

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
}
