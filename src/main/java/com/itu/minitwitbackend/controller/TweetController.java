package com.itu.minitwitbackend.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itu.minitwitbackend.repository.TweetRepository;
import com.itu.minitwitbackend.repository.entity.TweetEntity;

@RestController
@RequestMapping("/devops/tweet")
public class TweetController {

    private final TweetRepository tweetRepository;

    public TweetController(TweetRepository tweetRepository) {
        this.tweetRepository = tweetRepository;
    }

    @GetMapping("/getAllUserTweets/{username}")
    public ResponseEntity<List<TweetEntity>> getUserTweets(@PathVariable String username) {
        return ResponseEntity.ok(tweetRepository.findByUsername(username));
    }

    @GetMapping("/getAllTweets")
    public ResponseEntity<List<TweetEntity>> getAllTweets() {
        return ResponseEntity.ok(tweetRepository.findAll());
    }

    @PostMapping("/addTweet")
    public ResponseEntity<TweetEntity> addTweets(@RequestBody TweetEntity tweet) {
        return ResponseEntity.ok(tweetRepository.save(tweet));
    }
}
