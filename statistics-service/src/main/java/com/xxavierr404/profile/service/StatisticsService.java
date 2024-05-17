package com.xxavierr404.profile.service;

import com.xxavierr404.profile.dao.ChatTimeStatisticsRepository;
import com.xxavierr404.profile.dao.MessageStatisticsRepository;
import com.xxavierr404.profile.domain.ChatTimeStatistics;
import com.xxavierr404.profile.domain.MessageStatistics;
import com.xxavierr404.profile.dto.ChatTimeStatisticsCreateDto;
import com.xxavierr404.profile.dto.ChatTimeStatisticsResponseDto;
import com.xxavierr404.profile.dto.MessageStatisticsCreateDto;
import com.xxavierr404.profile.dto.MessageStatisticsResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final ChatTimeStatisticsRepository chatTimeStatisticsRepository;
    private final MessageStatisticsRepository messageStatisticsRepository;
    private final WebClient chatClient;

    @Transactional
    public void createStatistics(ChatTimeStatisticsCreateDto dto) {
        chatTimeStatisticsRepository.save(
                new ChatTimeStatistics(
                        null,
                        dto.getUserId(),
                        dto.getChatId(),
                        dto.getOnlineTimeMillis(),
                        Instant.now()
                )
        );
    }

    @Transactional
    public void createStatistics(MessageStatisticsCreateDto dto) {
        messageStatisticsRepository.save(
                new MessageStatistics(
                        null,
                        dto.getUserId(),
                        dto.getChatId(),
                        Instant.now()
                )
        );
    }

    public ChatTimeStatisticsResponseDto getChatTimeStats(
            Long chatId,
            Long userId,
            Long requesterId
    ) {
        validateRights(chatId, requesterId);
        var now = Instant.now();
        var stats = chatTimeStatisticsRepository.findByUserIdAndChatIdAndCreationTimeBetween(
                userId,
                chatId,
                now.minus(10, ChronoUnit.DAYS),
                now
        );
        var statsByDayOfWeek = stats.stream()
                .collect(
                        Collectors.groupingBy(
                                it -> it.getCreationTime()
                                        .atZone(ZoneId.systemDefault())
                                        .getDayOfWeek(),
                                Collectors.summingLong(ChatTimeStatistics::getPresenceTimeMillis)
                        )
                );
        return new ChatTimeStatisticsResponseDto(
                (double) stats.stream()
                        .mapToLong(ChatTimeStatistics::getPresenceTimeMillis)
                        .sum() / stats.size(),
                statsByDayOfWeek.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse(null)
        );
    }

    public MessageStatisticsResponseDto getMessageStats(
            Long chatId,
            Long userId,
            Long requesterId
    ) {
        validateRights(chatId, requesterId);
        var now = Instant.now();
        var stats = messageStatisticsRepository.findByUserIdAndChatIdAndSendingTimeBetween(
                userId,
                chatId,
                now.minus(10, ChronoUnit.DAYS),
                now
        );

        var statsByDayOfWeek = stats.stream()
                .collect(
                        Collectors.groupingBy(
                                it -> it.getSendingTime()
                                        .atZone(ZoneId.systemDefault())
                                        .getDayOfWeek(),
                                Collectors.summingInt(it -> 1)
                        )
                );
        return new MessageStatisticsResponseDto(
                (long) stats.size(),
                (double) statsByDayOfWeek.values().stream()
                        .mapToInt(it -> it).sum() / statsByDayOfWeek.size(),
                statsByDayOfWeek.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse(null)
        );
    }

    private void validateRights(Long chatId, Long userId) {
        if (!doesUserHaveManagerRights(chatId, userId)) {
            throw new IllegalStateException(
                    "У пользователя должны быть права менеджера для этого действия"
            );
        }
    }

    private boolean doesUserHaveManagerRights(Long chatId, Long userId) {
        return Boolean.TRUE.equals(chatClient
                .get()
                .uri(
                        "/api/v1/chat/%d/%d/check-rights"
                                .formatted(chatId, userId)
                )
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
                .bodyToMono(Boolean.class)
                .block());
    }
}
