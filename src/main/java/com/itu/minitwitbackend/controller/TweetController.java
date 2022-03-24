package com.itu.minitwitbackend.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itu.minitwitbackend.controller.api.model.TweetFlagRequest;
import com.itu.minitwitbackend.repository.entity.TweetEntity;
import com.itu.minitwitbackend.service.TweetService;
import com.microsoft.applicationinsights.TelemetryClient;

@RestController
@RequestMapping("/devops/tweet")
public class TweetController {

    private final TweetService tweetService;

    private final TelemetryClient telemetryClient;

    @Autowired
    public TweetController(TweetService tweetService, TelemetryClient telemetryClient) {
        this.tweetService = tweetService;
        this.telemetryClient = telemetryClient;
    }

    @GetMapping("/get-user-tweets/{username}")
    public ResponseEntity<List<TweetEntity>> getUserTweets(@PathVariable String username) {
        telemetryClient.trackEvent("get-user-tweets /is triggered");

        return ResponseEntity.ok(tweetService.findByUsername(username));
    }

    @GetMapping("/get-all-tweets/{batch-size}/{page-number}")
    public ResponseEntity<List<TweetEntity>> getAllTweets(@PathVariable(value = "batch-size", required = false) int batchSize,
                                                          @PathVariable(value = "page-number", required = false) int pageNumber) {
        telemetryClient.trackEvent("get-all-tweets /is triggered");
        if (batchSize == 0) batchSize = 100;
        return ResponseEntity.ok(tweetService.getAllTweetsSorted(batchSize, pageNumber));
    }

    @PostMapping("/add-tweet")
    public ResponseEntity<TweetEntity> addTweets(@RequestBody TweetEntity tweet) {
        telemetryClient.trackEvent("add-tweet /is triggered");

        return ResponseEntity.ok(tweetService.saveTweet(tweet));
    }

    @PostMapping("/update-Tweet-flag")
    public ResponseEntity<String> flagTweet(@RequestBody TweetFlagRequest tweetFlagRequest) {
        telemetryClient.trackEvent("update-Tweet-flag /is triggered");

        tweetService.flagTweet(tweetFlagRequest);
        return ResponseEntity.ok("flag updated successfully");
    }

}
