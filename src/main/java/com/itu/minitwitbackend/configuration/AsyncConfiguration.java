package com.itu.minitwitbackend.configuration;

import java.util.concurrent.Executor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

/**
 * AsyncConfiguration for the executor service.
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfiguration extends AsyncConfigurerSupport {

    @Override
    @Bean("executorService")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new
                ThreadPoolTaskExecutor();
        executor.setCorePoolSize(7);
        executor.setMaxPoolSize(10);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setKeepAliveSeconds(3);
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler
    getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("Async call failed: methodName={}", method.getName(), ex);
        };
    }
}
