package com.tk.eventmanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    // === Генерация токена ===
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email)                    // "sub" — кто
                .claim("role", role)               // кастомное поле
                .issuedAt(new Date())              // "iat" — когда создан
                .expiration(new Date(System.currentTimeMillis() + expirationMs))  // "exp"
                .signWith(getSigningKey())         // подпись
                .compact();
    }

    // === Извлечение email из токена ===
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // === Извлечение роли ===
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // === Валидация: не истёк ли? ===
    public boolean isTokenValid(String token) {
        try {
            Date expiration = extractClaim(token, Claims::getExpiration);
            return expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // === Извлечение любого claim ===
    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }

    // === Ключ подписи ===
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}