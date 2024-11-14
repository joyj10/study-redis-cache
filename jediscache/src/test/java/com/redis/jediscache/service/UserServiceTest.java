package com.redis.jediscache.service;

import com.redis.jediscache.domain.entity.User;
import com.redis.jediscache.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JedisPool jedisPool;

    @Mock
    private Jedis jedis;

    @InjectMocks
    private UserService userService;

    private final Long userId = 1L;
    private final String name = "test";
    private final String email = "test@example.com";
    private final String cacheKey = "user:" + userId + ":email";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(jedisPool.getResource()).thenReturn(jedis);
    }

    @DisplayName("캐시 이메일 조회 성공")
    @Test
    public void returnsEmailFromCache() {
        // 캐시에 값이 이미 존재할 때
        when(jedis.get(cacheKey)).thenReturn(email);

        String result = userService.getUserEmail(userId);

        // 캐시에서 가져오고 DB 조회는 하지 않음
        assertThat(result).isEqualTo(email);
        verify(jedis).get(cacheKey);
        verifyNoInteractions(userRepository);
    }

    @DisplayName("캐시가 아닌 DB 조회 성공")
    @Test
    public void fetchesEmailFromDbAndCachesIt() {
        // 캐시에 값이 없을 때
        when(jedis.get(cacheKey)).thenReturn(null);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User(userId, email, name, LocalDateTime.now(), LocalDateTime.now())));

        String result = userService.getUserEmail(userId);

        // DB에서 값을 가져와 캐시에 저장
        assertThat(result).isEqualTo(email);
        verify(jedis).get(cacheKey);
        verify(userRepository).findById(userId);
        verify(jedis).setex(cacheKey, 30, email);
    }

    @DisplayName("사용자 없으면 예외 발생 : EntityNotFoundException")
    @Test
    public void throwsExceptionWhenUserNotFound() {
        // 캐시와 DB 모두에서 사용자를 찾을 수 없는 경우
        when(jedis.get(cacheKey)).thenReturn(null);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserEmail(userId))
                .isInstanceOf(EntityNotFoundException.class);

        verify(jedis).get(cacheKey);
        verify(userRepository).findById(userId);
        verify(jedis, never()).setex(anyString(), anyInt(), anyString());
    }

}
