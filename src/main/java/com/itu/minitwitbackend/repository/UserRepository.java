package com.itu.minitwitbackend.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.itu.minitwitbackend.repository.entity.UserEntity;

public interface UserRepository extends MongoRepository<UserEntity, String> {
    Optional<UserEntity> findUserEntityByEmailAndAndUsername(String email, String username);

    Optional<UserEntity> findByUsernameAndPassword(String username, String password);
}
