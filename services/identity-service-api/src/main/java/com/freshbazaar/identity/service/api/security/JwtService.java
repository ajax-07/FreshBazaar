package com.freshbazaar.identity.service.api.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.freshbazaar.identity.service.api.utils.ServiceCommonConstant.TYPE;
import static com.freshbazaar.identity.service.api.utils.ServiceCommonConstant.TYPE_ACCESS;
import static com.freshbazaar.identity.service.api.utils.ServiceCommonConstant.TYPE_REFRESH;
import static com.freshbazaar.identity.service.api.utils.ServiceCommonConstant.USER_ID;

@Service
@Slf4j
public class JwtService {
    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${security.jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    /**
     * Generate Access Token
     */
    public String generateAccessToken(String userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID, userId);
        claims.put(TYPE, TYPE_ACCESS);

        return createToken(claims, username, accessTokenExpiration);
    }

    /**
     * Generate Refresh token
     */
    public String generateRefreshToken(String userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID, userId);
        claims.put(TYPE, TYPE_REFRESH);

        return createToken(claims, username, refreshTokenExpiration);
    }

    /**
     * Extract user from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Get userId from token
     */
    public String extractId(String token) {
        return extractClaim(token, Claims::getId);
    }

    /**
     * Extract Expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Check if the token is valid
     */
    public Boolean validateToken(String token, String userName) {
        String tokenUserName = extractUsername(token);
        return (tokenUserName.equals(userName) && !isTokenExpired(token));
    }

    /**
     * Check if the token is expired
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     *
     * @param token          jwt token
     * @param claimsResolver resolver
     * @param <T>            type of claim subject
     * @return username
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaim(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Get access token expiration in seconds
     */
    public Long getAccessTokenExpirationInSeconds() {
        return accessTokenExpiration / 1000;
    }


    /* -------------- Private methods starts here ------------------- */


    /**
     *
     * @param token token
     * @return Claims
     */
    private Claims extractAllClaim(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())    // Replaces .setSigningKey()
                .build()
                .parseSignedClaims(token)   // Replaces .parseClaimsJws()
                .getPayload();
    }

    /**
     * Create Token
     *
     * @param claims     map of user claim
     * @param username   username
     * @param expiration expiration time
     * @return String Token
     */
    private String createToken(Map<String, Object> claims, String username, Long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey())
                .compact();

    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
