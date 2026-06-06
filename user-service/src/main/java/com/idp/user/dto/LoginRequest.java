package com.idp.user.dto;

public record LoginRequest(
        String username,
        String password
) {
}