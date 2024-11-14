package com.redis.jediscache;

import com.redis.jediscache.domain.entity.User;
import com.redis.jediscache.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@RequiredArgsConstructor
public class JediscacheApplication implements ApplicationRunner {

    private final UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(JediscacheApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        userRepository.save(User.builder().name("a").email("a@test.com").build());
        userRepository.save(User.builder().name("b").email("b@test.com").build());
        userRepository.save(User.builder().name("c").email("c@test.com").build());
        userRepository.save(User.builder().name("d").email("d@test.com").build());
    }
}
