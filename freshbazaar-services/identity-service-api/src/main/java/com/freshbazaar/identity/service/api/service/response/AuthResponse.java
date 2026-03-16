package com.freshbazaar.identity.service.api.service.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private UUID userId; // UUID user-service
    private String accessToken;
    private String refreshToken;
    private Long expiresIn; // seconds
    @Builder.Default
    private String tokenType =  "Bearer";
}
