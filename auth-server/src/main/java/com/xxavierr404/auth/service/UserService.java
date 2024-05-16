package com.xxavierr404.auth.service;

import com.xxavierr404.auth.dao.UserRepository;
import com.xxavierr404.auth.domain.Role;
import com.xxavierr404.auth.domain.User;
import com.xxavierr404.auth.dto.UserRegisterDto;
import com.xxavierr404.auth.jwt.provider.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final WebClient profilesWebClient;
    private final JwtProvider jwtProvider;

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAllByOrganizationId(Long id) {
        return userRepository.findAllByOrganizationId(id);
    }

    @Transactional
    public void register(UserRegisterDto dto) {
        if (userRepository.findByLogin(dto.getLogin()).isPresent()) {
            throw new IllegalArgumentException("Логин занят");
        }

        if (
                dto.getRole().equals(Role.EMPLOYEE)
                && dto.getOrganizationId() == null
        ) {
            throw new IllegalArgumentException("У работников должен быть organizationId");
        }

        var user = dto.toEntity();
        user.setPassword(encoder.encode(user.getPassword()));
        user = userRepository.save(user);

        var jwt = jwtProvider.generateAccessToken(user);
        profilesWebClient
                .post()
                .uri("/api/v1/user-profile")
                .header("Authorization", "Bearer %s".formatted(jwt))
                .retrieve()
                .onStatus(
                        status -> !status.is2xxSuccessful(),
                        response -> Mono.error(new IllegalStateException(
                                "Не получилось создать профиль: %s".formatted(response.statusCode())
                        ))
                )
                .toBodilessEntity()
                .block();
    }
}
