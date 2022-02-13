package com.itu.minitwitbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itu.minitwitbackend.repository.entity.UserEntity;
import com.itu.minitwitbackend.service.UserService;

@RestController
@RequestMapping("/devops/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getUser/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUser(id).get());
    }

    @PostMapping("/createUser")
    public ResponseEntity<String> createUser(@RequestBody UserEntity user) {
        return new ResponseEntity<>(userService.createNewUser(user), HttpStatus.CREATED);
    }

    @GetMapping("/login/{username}/{password}")
    public ResponseEntity login(@PathVariable String username, @PathVariable String password) {
        userService.validateUserCredentials(username, password);
        return new ResponseEntity(HttpStatus.FOUND);
    }
}
