package com.idp.user.service;

import com.idp.user.dto.AuthResponse;
import com.idp.user.dto.LoginRequest;
import com.idp.user.dto.RegisterUserRequest;
import com.idp.user.dto.UserResponse;
import com.idp.user.entity.Role;
import com.idp.user.entity.User;
import com.idp.user.repository.UserRepository;
import com.idp.user.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public UserResponse register(
            RegisterUserRequest request) {

        validateUser(request);

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(
                        passwordEncoder.encode(
                                request.password()
                        )
                )
                .role(Role.DEVELOPER)
                .build();

        return mapToResponse(
                repository.save(user)
        );
    }

    public List<UserResponse> getAllUsers() {

        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public UserResponse getUser(
            Long id) {

        User user =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        return mapToResponse(user);
    }

    public UserResponse getByUsername(
            String username) {

        User user =
                repository.findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        return mapToResponse(user);
    }

    public AuthResponse login(
            LoginRequest request) {

        User user =
                repository.findByUsername(
                                request.username()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid credentials"
                                ));

        boolean matches =
                passwordEncoder.matches(
                        request.password(),
                        user.getPassword()
                );

        if (!matches) {

            throw new RuntimeException(
                    "Invalid credentials"
            );
        }

        String token =
                jwtService.generateToken(
                        user.getUsername(),
                        user.getRole().name()
                );

        return new AuthResponse(token);
    }

    private void validateUser(
            RegisterUserRequest request) {

        if (repository.findByUsername(
                request.username()
        ).isPresent()) {

            throw new RuntimeException(
                    "Username already exists"
            );
        }

        if (repository.findByEmail(
                request.email()
        ).isPresent()) {

            throw new RuntimeException(
                    "Email already exists"
            );
        }
    }

    private UserResponse mapToResponse(
            User user) {

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}