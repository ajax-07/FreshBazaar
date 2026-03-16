package com.freshbazaar.identity.service.api.service;

import com.freshbazaar.identity.service.api.database.entity.Identity;
import com.freshbazaar.identity.service.api.database.entity.RefreshToken;
import com.freshbazaar.identity.service.api.database.repository.IdentityRepository;
import com.freshbazaar.identity.service.api.database.repository.RefreshTokenRepository;
import com.freshbazaar.identity.service.api.exception.AccountLockedException;
import com.freshbazaar.identity.service.api.exception.DuplicateCredentialException;
import com.freshbazaar.identity.service.api.exception.InvalidCredentialException;
import com.freshbazaar.identity.service.api.exception.TokenExpiredException;
import com.freshbazaar.identity.service.api.security.JwtService;
import com.freshbazaar.identity.service.api.service.request.ChangePasswordRequest;
import com.freshbazaar.identity.service.api.service.request.LoginRequest;
import com.freshbazaar.identity.service.api.service.request.RegisterRequest;
import com.freshbazaar.identity.service.api.service.response.AuthResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {


    private final IdentityRepository identityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;


    /**
     * Register new identity for new user
     *
     * @param request register request dto
     * @return AuthResponse
     */
    @Transactional
    public AuthResponse Register(RegisterRequest request) {
        log.info("Registering new identity with username: {}", request.getUsername());
        if (identityRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateCredentialException("Username already exists: " + request.getUsername());
        }

        // save identity
        Identity identity = saveIdentity(request);
        log.info("New identity created with userId: {}", identity.getId());

        // generate token
        String accessToken = jwtService.generateAccessToken(identity.getUserId().toString(), request.getUsername());
        String refreshToken = jwtService.generateRefreshToken(identity.getUserId().toString(), request.getUsername());

        saveRefreshToken(identity, refreshToken);

        return getResponse(identity, accessToken, refreshToken);
    }

    /**
     *
     * @param request login request dto
     * @return AuthResponse
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt with username: {}", request.getUsername());

        // find identity
        Identity identity = identityRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialException("Invalid username or password"));

        // check if account is locked
        if(identity.getAccountLocked() && identity.getActive()) {
            throw new AccountLockedException("Account is inactive or locked.");
        }

        // verify password
        if(!passwordEncoder.matches(request.getPassword(), identity.getPassword())) {
            identity.recordFailedLogin();
            identityRepository.save(identity);
            log.warn("Failed login attempt for username: {}", request.getUsername());
            throw new InvalidCredentialException("Invalid username or password.");
        }

        identity.recordSuccessfulLogin();
        identityRepository.save(identity);

        log.info("User logged in successfully: {}", request.getUsername());

        String accessToken = jwtService.generateAccessToken(identity.getUserId().toString(), request.getUsername());
        String refreshToken = jwtService.generateRefreshToken(identity.getUserId().toString(), request.getUsername());

        // Revoke old refresh tokens and save new one
        refreshTokenRepository.revokeAllByIdentityId(identity.getId());
        saveRefreshToken(identity, refreshToken);

        return getResponse(identity, accessToken, refreshToken);
    }

    /**
     * Refresh the current token through refresh token
     *
     * @param refreshTokenValue refresh token
     * @return AuthResponse
     */
    @Transactional
    public AuthResponse refreshToken(String refreshTokenValue) {
        log.info("Refreshing access token");
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new InvalidCredentialException("Invalid refresh token"));

        if(!refreshToken.isValid()){
            throw new TokenExpiredException("Refresh token is expired or revoked");
        }

        Identity identity = refreshToken.getIdentity();

        // Generate new access token
        String accessToken = jwtService.generateAccessToken(identity.getUserId().toString(), identity.getUsername());
        log.info("Access token refreshed for userId: {}", identity.getUserId());

        return getResponse(identity, accessToken, refreshTokenValue);
    }

    /**
     * Change current password
     *
     * @param userId user ID
     * @param request change password request
     */
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        log.info("Password change request for userId: {}", userId);

        // find identity
        Identity identity = identityRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidCredentialException("User not found"));

        if(!passwordEncoder.matches(request.getCurrentPassword(), identity.getPassword())){
            throw new InvalidCredentialException("Current password is Invalid.");
        }

        identity.setPassword(passwordEncoder.encode(request.getNewPassword()));
        identity.setPasswordChangedAt(LocalDateTime.now());
        identityRepository.save(identity);

        // Revoke all refresh tokens (force re-login)
        refreshTokenRepository.revokeAllByIdentityId(identity.getId());

        log.info("Password changed successfully for userId: {}", userId);
    }

//    public void logout(String refreshTokenValue) {
//        log.info("Logout request");
//
//        refreshTokenRepository.findByToken(refreshTokenValue)
//                .ifPresent(token -> {
//                    token.setRevoked(true);
//                    refreshTokenRepository.save(token);
//                    log.info("Refresh token revoked for userId: {}", token.getIdentity().getUserId());
//                });
//    }

    /* -------------------------- private helper method -------------------------- */

    private Identity saveIdentity(RegisterRequest request) {
        Identity identity = Identity.builder()
                .userId(UUID.randomUUID())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))  // store hashed password
                .credentialType(request.getCredentialType())
                .build();

        return identityRepository.save(identity);
    }

    // Save refresh token to database
    private void saveRefreshToken(Identity identity, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .identity(identity)
                .token(token)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration/1000))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    private AuthResponse getResponse(Identity identity, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .userId(identity.getUserId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpirationInSeconds())
                .build();
    }
}
