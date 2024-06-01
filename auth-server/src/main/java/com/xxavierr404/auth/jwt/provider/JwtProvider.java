package com.xxavierr404.auth.jwt.provider;

import com.xxavierr404.auth.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtProvider {
    private final SecretKey jwtAccessSecret;

    public JwtProvider(
            @Value("${jwt.secret.access}") String jwtAccessSecret
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
    }

    public String generateAccessToken(@NonNull User user) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusDays(1).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .subject(user.getLogin())
                .expiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .claim("id", user.getId())
                .claim("role", user.getRole())
                .claim("firstName", user.getFirstName())
                .claim("organizationId", user.getOrganizationId())
                .compact();
    }
}
