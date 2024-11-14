package com.redis.jediscache.service;

import com.redis.jediscache.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JedisPool jedisPool;

    @Transactional(readOnly = true)
    public String getUserEmail(Long id) {
        try (Jedis jedis = jedisPool.getResource()) {
            // 1. request to cache
            String key = jedisUserKey(id);
            String userEmail = jedis.get(key);
            if (userEmail != null) {
                return userEmail;
            }

            // 2. else request to db
            userEmail = userRepository.findById(id)
                    .orElseThrow(EntityNotFoundException::new)
                    .getEmail();

            // 3. save cache
            jedis.setex(key, 30, userEmail);

            // end
            return userEmail;
        }
    }

    private String jedisUserKey(Long id) {
        return "user:%d:email".formatted(id);
    }
}
