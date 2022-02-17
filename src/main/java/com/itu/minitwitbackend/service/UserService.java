package com.itu.minitwitbackend.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<UserEntity> getUser(String userId) {
        log.info("getting user with id {} ", userId);
        var user = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found in the database"));
        return Optional.of(user);
    }

    public String createNewUser(UserEntity user) {
        log.info("creating a new user {} ", user.toString());

        if (repository.findUserEntityByUsername(user.getUsername()).isPresent()) {
            log.error("The username is already taken");
            throw new UserAlreadyExistsException("The username is already taken");
        }
        // TODO make the url for the image
        var userId = repository.save(user).getId();
        log.info("new created userId {} ", userId);
        return userId;
    }

    public void validateUserCredentials(String username, String password) {
        repository.findByUsernameAndPassword(username, password)
                .ifPresentOrElse(u ->
                                log.info("user {} has valid credentials, log in successfully ", username)
                        , () -> {
                            log.error("user {} has valid credentials, log in failed", username);
                            throw new InvalidCredentialsException("credentials are not valid");
                        });

    }
}
