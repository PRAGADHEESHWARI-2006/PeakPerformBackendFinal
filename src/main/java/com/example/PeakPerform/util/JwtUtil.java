package com.example.PeakPerform.util;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;



import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiry-ms}")
    private long accessTokenExpiry;

    @Value("${jwt.refresh-token-expiry-ms}")
    private long refreshTokenExpiry;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Generate Access Token
    public String generateAccessToken(UserDetails userDetails) {

        return buildToken(
                new HashMap<>(),
                userDetails.getUsername(),
                accessTokenExpiry);
    }

    // Generate Refresh Token
    public String generateRefreshToken(UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("type", "refresh");

        return buildToken(
                claims,
                userDetails.getUsername(),
                refreshTokenExpiry);
    }

    private String buildToken(Map<String, Object> extraClaims,
            String subject,
            long expiryMs) {

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract Email
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token,
            Function<Claims, T> claimsResolver) {

        Claims claims = getClaims(token);

        return claimsResolver.apply(claims);
    }

    // Extract Role
    public String extractRole(String token) {
        return extractClaim(token,
                claims -> claims.get("role", String.class));
    }

    // Validate Token
    public boolean isTokenValid(String token,
            UserDetails userDetails) {

        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token));
    }

    // Check Expiry
    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    // Get Claims
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
