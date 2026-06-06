package com.idp.user.dto;

public record RegisterUserRequest(
        String username,
        String email,
        String password
) {
}