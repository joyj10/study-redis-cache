package com.redis.springcache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpringcacheApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringcacheApplication.class, args);
    }
}
