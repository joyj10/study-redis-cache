package com.redis.jediscache.domain.repository;

import com.redis.jediscache.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
