package com.xxavierr404.auth.service;

import com.xxavierr404.auth.domain.User;
import com.xxavierr404.auth.jwt.JwtRequest;
import com.xxavierr404.auth.jwt.JwtResponse;
import com.xxavierr404.auth.jwt.provider.JwtProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public JwtResponse login(@NonNull JwtRequest authRequest) {
        final User user = userService.findByLogin(authRequest.getLogin())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            return new JwtResponse(accessToken);
        } else {
            throw new IllegalArgumentException("Неправильный пароль");
        }
    }
}
