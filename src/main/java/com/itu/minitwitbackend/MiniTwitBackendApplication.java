package com.itu.minitwitbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableCaching
public class MiniTwitBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniTwitBackendApplication.class, args);
    }

}
