package com.idp.user.config;

import com.idp.user.entity.Role;
import com.idp.user.entity.User;
import com.idp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer
        implements CommandLineRunner {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (repository.findByUsername("admin")
                .isEmpty()) {

            User admin = User.builder()
                    .username("admin")
                    .email("admin@test.com")
                    .password(
                            passwordEncoder.encode(
                                    "admin123"
                            )
                    )
                    .role(Role.ADMIN)
                    .build();

            repository.save(admin);
        }
    }
}