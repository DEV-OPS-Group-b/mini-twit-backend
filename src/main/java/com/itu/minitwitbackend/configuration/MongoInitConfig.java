package com.itu.minitwitbackend.configuration;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.itu.minitwitbackend.repository.UserRepository;
import com.itu.minitwitbackend.repository.entity.UserEntity;

@Configuration
@EnableMongoRepositories(basePackageClasses = UserRepository.class)
public class MongoInitConfig {

    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository) {
        return strings -> userRepository.saveAll(createMockUserEntity());
    }

    private List<UserEntity> createMockUserEntity() {
        return new ArrayList<>() {
            {
                add(UserEntity.builder()
                        .username("me")
                        .email("me@gmail.com")
                        .password("password")
                        .following(new ArrayList<>() {
                            {
                                add("you");
                                add("he");
                            }
                        }).build());

                add(UserEntity.builder()
                        .username("you")
                        .email("you@gmail.com")
                        .password("password")
                        .following(new ArrayList<>() {
                            {
                                add("me");
                                add("he");
                            }
                        }).build());

                add(UserEntity.builder()
                        .username("he")
                        .email("he@gmail.com")
                        .password("password")
                        .following(new ArrayList<>()).build());
            }
        };

    }
}
