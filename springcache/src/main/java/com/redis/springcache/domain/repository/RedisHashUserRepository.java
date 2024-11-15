package com.redis.springcache.domain.repository;

import com.redis.springcache.domain.entity.RedisHashUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedisHashUserRepository extends CrudRepository<RedisHashUser, Long> {
    Optional<RedisHashUser> findByEmail(String email);
}
