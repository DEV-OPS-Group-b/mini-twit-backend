package com.itu.minitwitbackend.controller;

import java.util.List;
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

@RestController
@RequestMapping("/devops/tweet")
public class TweetController {

    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @GetMapping("/getAllUserTweets/{username}")
    public ResponseEntity<List<TweetEntity>> getUserTweets(@PathVariable String username) {
        return ResponseEntity.ok(tweetService.findByUsername(username));
    }

    @GetMapping("/getAllTweets")
    public ResponseEntity<List<TweetEntity>> getAllTweets() {

        return ResponseEntity.ok(tweetService.getAllTweetsSorted());
    }

    @PostMapping("/addTweet")
    public ResponseEntity<TweetEntity> addTweets(@RequestBody TweetEntity tweet) {
        return ResponseEntity.ok(tweetService.saveTweet(tweet));
    }

    @PostMapping("/update-Tweet-flag")
    public ResponseEntity<String> flagTweet(@RequestBody TweetFlagRequest tweetFlagRequest) {
        tweetService.flagTweet(tweetFlagRequest);
        return ResponseEntity.ok("flag updated successfully");
    }

}
