package com.example.ResourceReserve.service;

import com.example.ResourceReserve.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class JwtService {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.refresh-secret}")
    private String jwtRefreshSecret;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    @Value("${jwt.refresh-expiration}")
    private Long jwtRefreshExpiration;
    
    @Value("${jwt.issuer}")
    private String jwtIssuer;
    
    @Value("${jwt.audience}")
    private String jwtAudience;
    
    public String getJwtSecret() {
        return jwtSecret;
    }
    
    public String getJwtRefreshSecret() {
        return jwtRefreshSecret;
    }
    
    public Long getJwtRefreshExpiration() {
        return jwtRefreshExpiration;
    }
    
    private SecretKey getSigningKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
    public String generateAccessToken(User user) {
        return generateToken(user, jwtSecret, jwtExpiration);
    }
    
    public String generateRefreshToken(User user) {
        return generateToken(user, jwtRefreshSecret, jwtRefreshExpiration);
    }
    
    private String generateToken(User user, String secret, Long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());
        claims.put("permissions", user.getPermissions());
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuer(jwtIssuer)
                .setAudience(jwtAudience)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey(secret), Jwts.SIG.HS256)
                .compact();
    }
    
    public Claims extractAllClaims(String token, String secret) {
        return Jwts.parser()
                .verifyWith(getSigningKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    public String extractEmail(String token, String secret) {
        return extractAllClaims(token, secret).getSubject();
    }
    
    public Date extractExpiration(String token, String secret) {
        return extractAllClaims(token, secret).getExpiration();
    }
    
    public boolean isTokenExpired(String token, String secret) {
        try {
            return extractExpiration(token, secret).before(new Date());
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }
    
    public boolean validateToken(String token, String secret) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey(secret))
                    .build()
                    .parseSignedClaims(token);
            return !isTokenExpired(token, secret);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
    
    public String extractUserId(String token, String secret) {
        return extractAllClaims(token, secret).get("userId", String.class);
    }
    
    public String extractRole(String token, String secret) {
        return extractAllClaims(token, secret).get("role", String.class);
    }
    
    @SuppressWarnings("unchecked")
    public Set<String> extractPermissions(String token, String secret) {
        return (Set<String>) extractAllClaims(token, secret).get("permissions");
    }
} 