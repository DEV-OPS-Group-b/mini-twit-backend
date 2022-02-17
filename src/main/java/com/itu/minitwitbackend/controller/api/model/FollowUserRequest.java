package com.itu.minitwitbackend.controller.api.model;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FollowUserRequest {
    private String currentUsername;
    private String targetUsername;
}
