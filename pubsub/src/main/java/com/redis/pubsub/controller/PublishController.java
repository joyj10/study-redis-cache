package com.redis.pubsub.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PublishController {

    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping("/events/users/deregister")
    public void publishUserDeregisterEvent(String message) {
        redisTemplate.convertAndSend("users:unregister", message);
    }
}
