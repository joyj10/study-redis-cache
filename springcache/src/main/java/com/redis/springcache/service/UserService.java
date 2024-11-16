package com.redis.springcache.service;

import com.redis.springcache.domain.entity.RedisHashUser;
import com.redis.springcache.domain.entity.User;
import com.redis.springcache.domain.repository.RedisHashUserRepository;
import com.redis.springcache.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static com.redis.springcache.config.CacheConfig.CACHE1;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, User> userRedisTemplate;
    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final RedisHashUserRepository redisHashUserRepository;

    /**
     * 사용자 조회(id)
     * - 캐시 : userRedisTemplate 활용
     * @param id 사용자 id
     * @return User
     */
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

    /**
     * 사용자 조회(이름)
     * - 캐시 : objectRedisTemplate 활용
     * @param name 사용자 이름
     * @return User
     */
    public User getUserByName(final String name){
        // 1. cache get
        String userCacheKey = getUserCacheKey("name", name);
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

    /**
     * 사용자 조회(이메일)
     * - 캐시 : RedisHashRepository 활용
     * @param email 이메일
     * @return User
     */
    public RedisHashUser getUserByEmail(String email) {
        return redisHashUserRepository.findByEmail(email).orElseGet(() -> {
            User user = userRepository.findByEmail(email).orElseThrow();
            return redisHashUserRepository.save(
                    RedisHashUser.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .createdAt(user.getCreatedAt())
                            .updatedAt(user.getUpdatedAt())
                            .build()
            );
        });
    }

    private static String getUserIdCacheKey(Long id) {
        return "user:id:%d".formatted(id);
    }

    private static String getUserCacheKey(String type, String value) {
        return "user:%s:%s".formatted(type, value);
    }


    /**
     * 사용자 조회(id)
     * - 캐시 : spring cache 활용
     * @param id 사용자 id
     * @return User
     */
    @Cacheable(cacheNames = CACHE1, key = "'user:' + #id")
    public User getUserById(final Long id){
        return userRepository.findById(id).orElseThrow();
    }

}
