package com.freshbazaar.identity.dto;

public record AuthResponse(
        String accessToken,
        long expiresIn
) {}
