package com.freshbazaar.identity.service.api.controller;

import com.freshbazaar.identity.service.api.service.AuthService;
import com.freshbazaar.identity.service.api.service.request.ChangePasswordRequest;
import com.freshbazaar.identity.service.api.service.request.LoginRequest;
import com.freshbazaar.identity.service.api.service.request.RegisterRequest;
import com.freshbazaar.identity.service.api.service.response.ApiResponse;
import com.freshbazaar.identity.service.api.service.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/auth")
@Tag(name = "Authentication", description = "Identity and authentication APIs")
public class AuthController {
    private final AuthService authService;

    /**
     *  Register new identity
     *  This ONLY creates authentication credentials
     *  Call user-service next to create profile
     *
     * @param request register request dto
     * @return ApiResponse<AuthResponse>
     */
    @PostMapping("/register")
    @Operation(summary = "Register new identity", description = "Creates authentication credentials and returns userId for user-service")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Received request to register new identity for user {}", request.getUsername());

        AuthResponse response = authService.Register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Identity created successfully. Please complete profile in user-service.",  response));

    }

    /**
     * Login with credentials
     *
     * @param request login request dto
     * @return ApiResponse<AuthResponse>
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received request to login for user {}", request.getUsername());
        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success("Login successful",  response)
        );
    }

    /**
     * Refresh the current token
     *
     * @param refreshToken refresh token value
     * @return ApiResponse<AuthResponse>
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Get new access token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        log.info("Received request to refresh token");

        AuthResponse response = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(
                ApiResponse.success("Refresh successful",  response)
        );
    }

    /**
     * Change current password
     *
     * @param userId current user id
     * @param request password change request dto
     * @return ApiResponse<AuthResponse>
     */
    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change user password")
    public ResponseEntity<ApiResponse<AuthResponse>> changePassword(
            @RequestHeader("User-Id") UUID userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("Password change request for userId: {}", userId);

        authService.changePassword(userId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Password changed successfully. Please login again.", null)
        );
    }

    /**
     * Logout user
     * @param refreshToken refresh token
     * @return ApiResponse<AuthResponse>
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Revoke refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> logout( @RequestHeader("Refresh-Token") String refreshToken) {
        log.info("Received request to logout");

//        authService.logout(refreshToken);

        return ResponseEntity.ok(
                ApiResponse.success("Logged out successfully", null)
        );
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if service is running")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(
                ApiResponse.success("Identity service is running", null)
        );
    }

}
