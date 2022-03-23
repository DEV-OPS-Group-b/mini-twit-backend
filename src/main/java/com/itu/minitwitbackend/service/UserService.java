package com.itu.minitwitbackend.service;

import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Cacheable("findUser")
    public Optional<UserEntity> getUser(String username) {
        log.info("getting user with username {} ", username);
        var user = userRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found in the database " + username));
        return Optional.of(user);
    }

    public String createNewUser(UserEntity user) {
        log.info("creating a new user {} ", user.toString());

        if (userRepository.findUserEntityByUsername(user.getUsername()).isPresent()) {
            log.error("The username is already taken");
            throw new UserAlreadyExistsException("The username is already taken");
        }
        user.setIsAdmin(false);
        var userId = userRepository.save(user).getId();
        log.info("newly created userId {} ", userId);
        return userId;
    }

    public void validateUserCredentials(String username, String password) {
        userRepository.findByUsernameAndPassword(username, password)
                .ifPresentOrElse(u ->
                                log.info("user {} has valid credentials, log in successfully ", username)
                        , () -> {
                            log.error("user {} has invalid credentials, log in failed", username);
                            throw new InvalidCredentialsException("credentials are invalid");
                        });

    }

    public void followUser(FollowUserRequest followUserRequest) {
        Optional<UserEntity> currentUser;
        try {
            currentUser = getUser(followUserRequest.getCurrentUsername());
        } catch (UserNotFoundException e) {
            log.error("recover from database delete on user {} ", followUserRequest.getCurrentUsername());
            var newUser = UserEntity.builder().username(followUserRequest.getCurrentUsername()).password("password").isAdmin(false).build();
            currentUser = Optional.of(userRepository.save(newUser));
        }

        updateFollowing(currentUser, followUserRequest);
        log.info("user {} want to follow user: {}", followUserRequest.getCurrentUsername(), followUserRequest.getTargetUsername());
        userRepository.save(currentUser.get());
    }

    private void updateFollowing(Optional<UserEntity> currentUser, FollowUserRequest followUserRequest) {
        var followers = currentUser.get().getFollowing();
        if (followers != null && followers.size() > 0) {
            if (followers.contains(followUserRequest.getTargetUsername())) {
                log.info("the user {} is already following username : {}",
                        followUserRequest.getCurrentUsername(), followUserRequest.getTargetUsername());
                return;
            }
            log.info(" adding username {} to the following list of user {} ", followUserRequest.getTargetUsername(),
                    followUserRequest.getCurrentUsername());
            currentUser.get().getFollowing().add(followUserRequest.getTargetUsername());
        } else {
            log.info(" adding username {} to the following list of user {} ", followUserRequest.getTargetUsername(),
                    followUserRequest.getCurrentUsername());
            currentUser.get().setFollowing(new ArrayList<>() {
                {
                    add(followUserRequest.getTargetUsername());
                }
            });
        }
    }

    public void unfollowUser(FollowUserRequest followUserRequest) {
        var currentUser = getUser(followUserRequest.getCurrentUsername());
        log.info("user {} want to unfollow user: {}", followUserRequest.getCurrentUsername(), followUserRequest.getTargetUsername());
        var followers = currentUser.get().getFollowing();
        if (followers != null && followers.contains(followUserRequest.getTargetUsername())) {

            log.info("removing username {} from the following list of username {} ",
                    followUserRequest.getTargetUsername(), followUserRequest.getCurrentUsername());

            followers.remove(followUserRequest.getTargetUsername());
            userRepository.save(currentUser.get());
        } else {
            log.info("the username {} does not exist in the following list of username {} ",
                    followUserRequest.getTargetUsername(), followUserRequest.getCurrentUsername());
        }
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

