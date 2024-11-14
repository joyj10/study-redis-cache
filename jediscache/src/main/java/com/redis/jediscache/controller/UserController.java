package com.redis.jediscache.controller;

import com.redis.jediscache.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users/{id}/email")
    public String getUserEmail(@PathVariable Long id) {
        return userService.getUserEmail(id);
    }
}
