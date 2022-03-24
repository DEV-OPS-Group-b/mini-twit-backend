package com.itu.minitwitbackend.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itu.minitwitbackend.controller.api.model.FollowUserRequest;
import com.itu.minitwitbackend.repository.entity.UserEntity;
import com.itu.minitwitbackend.service.UserService;
import com.microsoft.applicationinsights.TelemetryClient;

@RestController
@Validated
@RequestMapping("/devops/user")
public class UserController {

    private final UserService userService;
    private final TelemetryClient telemetryClient;
    @Autowired
    public UserController(UserService userService, TelemetryClient telemetryClient) {
        this.userService = userService;
        this.telemetryClient = telemetryClient;
    }

    @GetMapping("/get-user/{username}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable String username) {
        telemetryClient.trackEvent("get-user-by-id /is triggered");

        return ResponseEntity.ok(userService.getUser(username).get());
    }

    @PostMapping("/register")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserEntity user) {
        telemetryClient.trackEvent("register /is triggered");

        userService.createNewUser(user);
        return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/login/{username}/{password}")
    public ResponseEntity login(@PathVariable String username, @PathVariable String password) {
        telemetryClient.trackEvent("login /is triggered");

        userService.validateUserCredentials(username, password);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/follow")
    public ResponseEntity<String> followUser(@RequestBody FollowUserRequest followUserRequest) {
        telemetryClient.trackEvent("follow-user /is triggered");
        userService.followUser(followUserRequest);
        return new ResponseEntity<>("done", HttpStatus.OK);
    }

    @PostMapping("/unfollow")
    public ResponseEntity<String> unfollowUser(@Valid @RequestBody FollowUserRequest followUserRequest) {
        telemetryClient.trackEvent("unfollow-user /is triggered");
        userService.unfollowUser(followUserRequest);
        return new ResponseEntity<>("done", HttpStatus.OK);
    }

    @PostMapping("/isFollowing")
    public ResponseEntity<Boolean> isFollowing(@Valid @RequestBody FollowUserRequest followUserRequest) {
        telemetryClient.trackEvent("is-following /is triggered");
        return new ResponseEntity<>(userService.isFollowing(followUserRequest), HttpStatus.OK);
    }
}
