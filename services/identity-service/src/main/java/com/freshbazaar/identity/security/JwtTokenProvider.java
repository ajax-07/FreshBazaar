package com.freshbazaar.identity.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import static com.freshbazaar.identity.utils.CommonConstants.EXPIRATION_PROP;
import static com.freshbazaar.identity.utils.CommonConstants.SECRET_KEY_PROP;

@Component
public class JwtTokenProvider {
    @Value(SECRET_KEY_PROP)
    private String secret;

    @Value(EXPIRATION_PROP)
    private long expiration;

    public String generateToken(UUID userId){
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(key)
                .compact();
    }
}
