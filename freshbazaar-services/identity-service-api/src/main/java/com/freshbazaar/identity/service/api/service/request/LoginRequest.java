package com.freshbazaar.identity.service.api.service.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "username is required")
    private String username; // email or phone

    @NotBlank(message = "password is required")
    private String password;
}
