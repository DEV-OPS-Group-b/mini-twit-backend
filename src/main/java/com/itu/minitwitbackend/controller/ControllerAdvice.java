package com.itu.minitwitbackend.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.itu.minitwitbackend.exception.InvalidCredentialsException;
import com.itu.minitwitbackend.exception.UserAlreadyExistsException;
import com.itu.minitwitbackend.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@org.springframework.web.bind.annotation.ControllerAdvice
@Slf4j
public class ControllerAdvice {

    @ExceptionHandler(InvalidCredentialsException.class)

    public ResponseEntity<ErrorInfo> handleInvalidCredentialsException(
            HttpServletRequest request,
            InvalidCredentialsException ex) {
        log.warn("InvalidCredentialsException message={}", ex.getMessage());
        return handleException(request, ex.getMessage(), ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorInfo> handleUserAlreadyExistsException(
            HttpServletRequest request,
            UserAlreadyExistsException ex) {
        log.warn("UserAlreadyExistsException message={}", ex.getMessage());
        return handleException(request, ex.getMessage(), ex.getMessage(), HttpStatus.ALREADY_REPORTED);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleUserNotFoundException(
            HttpServletRequest request,
            UserNotFoundException ex) {
        log.warn("UserNotFoundException message={}", ex.getMessage());
        return handleException(request, ex.getMessage(), ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ErrorInfo> handleException(HttpServletRequest request, String msg,
                                                      String errorMessage, HttpStatus httpStatus) {
        ErrorInfo result = ErrorInfo.builder()
                .message(msg)
                .error(errorMessage)
                .path(request.getServletPath())
                .status(httpStatus.value())
                .timestamp(LocalDateTime.now(ZoneId.of("UTC")).toString()).build();

        return new ResponseEntity<>(result, httpStatus);
    }


    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    private static class ErrorInfo {
        private String timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private ErrorDetail[] errors;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    private static class ErrorDetail {
        private String code;
        private String defaultMessage;
        private String objectName;
        private String field;

    }
}
