package com.itu.minitwitbackend.service;

import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.itu.minitwitbackend.controller.api.model.FollowUserRequest;
import com.itu.minitwitbackend.exception.InvalidCredentialsException;
import com.itu.minitwitbackend.exception.UserAlreadyExistsException;
import com.itu.minitwitbackend.exception.UserNotFoundException;
import com.itu.minitwitbackend.repository.UserRepository;
import com.itu.minitwitbackend.repository.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
    private final UserRepository repository;

    private final MongoTemplate template;

    @Autowired
    public UserService(UserRepository repository, MongoTemplate template) {
        this.repository = repository;
        this.template = template;
    }

    public Optional<UserEntity> getUser(String username) {
        log.info("getting user with username {} ", username);
        var user = repository.findUserEntityByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found in the database" + username));
        return Optional.of(user);
    }

    public void createNewUser(UserEntity user) {
        log.info("creating a new user {} ", user.toString());

        if (repository.findUserEntityByUsername(user.getUsername()).isPresent()) {
            log.error("The username is already taken");
            throw new UserAlreadyExistsException("The username is already taken");
        }
        // TODO make the url for the image
        user.setIsAdmin(false);
        var userId = repository.save(user).getId();
        log.info("newly created userId {} ", userId);
    }

    public void validateUserCredentials(String username, String password) {
        repository.findByUsernameAndPassword(username, password)
                .ifPresentOrElse(u ->
                                log.info("user {} has valid credentials, log in successfully ", username)
                        , () -> {
                            log.error("user {} has invalid credentials, log in failed", username);
                            throw new InvalidCredentialsException("credentials are invalid");
                        });

    }

    public void followUser(FollowUserRequest followUserRequest) {
        var currentUser = getUser(followUserRequest.getCurrentUsername());
        updateFollowing(currentUser, followUserRequest);
        repository.save(currentUser.get());
    }

    private void updateFollowing(Optional<UserEntity> currentUser, FollowUserRequest followUserRequest) {
        var followers = currentUser.get().getFollowing();
        if (followers != null && followers.size() > 0)
            currentUser.get().getFollowing().add(followUserRequest.getTargetUsername());
        else {
            currentUser.get().setFollowing(new ArrayList<>() {
                {
                    add(followUserRequest.getTargetUsername());
                }
            });
        }
    }

    public void unfollowUser(FollowUserRequest followUserRequest) {
        var currentUser = getUser(followUserRequest.getCurrentUsername());
        var followers = currentUser.get().getFollowing();
        if (followers != null && followers.contains(followUserRequest.getTargetUsername())) {
            followers.remove(followUserRequest.getTargetUsername());
        }
        repository.save(currentUser.get());
    }

    public boolean isFollowing(FollowUserRequest followUserRequest) {
        var currentUser = getUser(followUserRequest.getCurrentUsername());
        var followers = currentUser.get().getFollowing();
        if (followers != null) {

            return followers.contains(followUserRequest.getTargetUsername());
        }
        return false;
    }
}

