package com.redis.springcache.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.redis.springcache.domain.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    // redis 적재 시 : 특정 객체 지정 변환 하는 방법
    @Bean
    RedisTemplate<String, User> userRedisTemplate(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)    // 역직렬화 시 모르는 속성이 있더라도 예외 발생시키지 않고 무시
                .registerModule(new JavaTimeModule())   // 자바 8 이상의 날짜 및 시간 처리를 위한 모듈 등록
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);   // 날짜를 타임스탬프 대신 ISO 8601 형식으로 직렬화

        RedisTemplate<String, User> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);   // Redis 연결 팩토리를 설정해 RedisTemplate을 Spring Boot 빈에 등록된 연결 팩토리로 설정
        template.setKeySerializer(new StringRedisSerializer()); // Redis 키를 문자열로 직렬화하도록 설정
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, User.class)); // Redis 값 직렬화 위해 Jackson2JsonRedisSerializer 사용
        return template;
    }

    // redis 적재 시 : 일반적인 객체 변환 하는 방법
    @Bean
    RedisTemplate<String, Object> ojbectRedisTemplate(RedisConnectionFactory connectionFactory) {
        PolymorphicTypeValidator polymorphicTypeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)
                .build();

        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .activateDefaultTyping(polymorphicTypeValidator, ObjectMapper.DefaultTyping.NON_FINAL) // 해당 클래스와 패키지 정보 포함해서 redis 저장해서 오류 발생 하지 않음
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)); // Redis 값 직렬화 위해 일반적인 GenericJackson2JsonRedisSerializer 사용
        return template;
    }
}
