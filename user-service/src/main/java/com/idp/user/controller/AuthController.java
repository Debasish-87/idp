package com.idp.user.controller;

import com.idp.user.dto.AuthResponse;
import com.idp.user.dto.LoginRequest;
import com.idp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest request) {

        return userService.login(request);
    }
}