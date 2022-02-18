package com.itu.minitwitbackend.controller.api.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TweetFlagRequest {
    @NotEmpty(message = "please provide tweet id")
    private String tweetId;
    @NotNull
    private Boolean flag;
}
