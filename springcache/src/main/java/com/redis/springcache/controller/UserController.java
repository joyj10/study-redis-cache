package com.redis.springcache.controller;

import com.redis.springcache.domain.entity.RedisHashUser;
import com.redis.springcache.domain.entity.User;
import com.redis.springcache.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/users/name")
    public ResponseEntity<User> getUserByName(@RequestParam(name = "name") String name) {
        return ResponseEntity.ok(userService.getUserByName(name));
    }

    @GetMapping("/users/email")
    public ResponseEntity<RedisHashUser> getUserByEmail(@RequestParam(name = "email") String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }
}
