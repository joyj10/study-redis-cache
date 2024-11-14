package com.redis.springcache.service;

import com.redis.springcache.domain.entity.User;
import com.redis.springcache.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, User> userRedisTemplate;
    private final RedisTemplate<String, Object> objectRedisTemplate;

    @Transactional(readOnly = true)
    public User getUser(final Long id){
        // 1. cache get
        String userCacheKey = getUserIdCacheKey(id);
        User cachedUser = userRedisTemplate.opsForValue().get(userCacheKey);
        if (cachedUser != null) {
            return cachedUser;
        }

        // 2. db get
        User user = userRepository.findById(id).orElseThrow();

        // 3. cache set
        userRedisTemplate.opsForValue().set(userCacheKey, user, Duration.ofSeconds(30));

        return user;
    }

    @Transactional(readOnly = true)
    public User getUserByName(final String name){
        // 1. cache get
        String userCacheKey = getUserNameCacheKey(name);
        User cachedUser = (User) objectRedisTemplate.opsForValue().get(userCacheKey);
        if (cachedUser != null) {
            return cachedUser;
        }

        // 2. db get
        User user = userRepository.findByName(name).orElseThrow();

        // 3. cache set
        objectRedisTemplate.opsForValue().set(userCacheKey, user, Duration.ofSeconds(30));

        return user;
    }

    private static String getUserIdCacheKey(Long id) {
        return "user:id:%d".formatted(id);
    }

    private static String getUserNameCacheKey(String name) {
        return "user:name:%s".formatted(name);
    }
}
