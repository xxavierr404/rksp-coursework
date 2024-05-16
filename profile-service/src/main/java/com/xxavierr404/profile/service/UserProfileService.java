package com.xxavierr404.profile.service;

import com.xxavierr404.profile.dao.UserProfileRepository;
import com.xxavierr404.profile.domain.User;
import com.xxavierr404.profile.domain.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final WebClient authClient;

    public Optional<UserProfile> findById(Long id) {
        return userProfileRepository.findById(id);
    }

    public UserProfile create(UserProfile userProfile) {
        if (findById(userProfile.getId()).isPresent()) {
            throw new IllegalArgumentException("Профиль с таким id уже существует");
        }

        return userProfileRepository.save(userProfile);
    }

    public void update(UserProfile userProfile) {
        var oldProfile = userProfileRepository.findById(userProfile.getId())
                .orElseThrow(() -> new IllegalArgumentException("Профиль не существует"));

        oldProfile.setDescription(userProfile.getDescription());
        oldProfile.setPosition(userProfile.getPosition());

        userProfileRepository.save(oldProfile);
    }

    public User getUser(Long userId) {
        return authClient.get()
                .uri("/api/v1/user-info/%d".formatted(userId))
                .retrieve()
                .onStatus(
                        status -> !status.is2xxSuccessful(),
                        response -> Mono.error(
                                new IllegalStateException(
                                        "Не удалось проверить наличие прав: %s"
                                                .formatted(response.statusCode())
                                )
                        )
                )
                .bodyToMono(User.class)
                .block();
    }
}
