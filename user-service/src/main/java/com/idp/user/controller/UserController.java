package com.idp.user.controller;

import com.idp.user.dto.RegisterUserRequest;
import com.idp.user.dto.UserResponse;
import com.idp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public UserResponse register(
            @RequestBody RegisterUserRequest request) {

        return userService.register(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserResponse> getAllUsers() {

        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getUser(
            @PathVariable Long id) {

        return userService.getUser(id);
    }

    @GetMapping("/username/{username}")
    public UserResponse getByUsername(
            @PathVariable String username) {

        return userService.getByUsername(username);
    }
}