package com.xxavierr404.profile.filter;

import com.xxavierr404.profile.dto.ChatTimeStatisticsCreateDto;
import com.xxavierr404.profile.dto.MessageCreateDto;
import com.xxavierr404.profile.dto.MessageStatisticsCreateDto;
import com.xxavierr404.profile.security.UserAuthContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class StatisticsMessageInterceptor implements ChannelInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsMessageInterceptor.class);

    private final WebClient statisticsClient;
    private final CompositeMessageConverter converter;

    private final Map<Long, Map<Long, Instant>> sessionStarts = new HashMap<>();
    private final Map<String, Long> sessionIdToUserId = new HashMap<>();

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand().equals(StompCommand.SUBSCRIBE)) {
            var userId = getUserId();
            sessionIdToUserId.put(accessor.getSessionId(), userId);
            var chatId = Long.parseLong(accessor.getDestination().split("/")[3]);
            sessionStarts.putIfAbsent(userId, new HashMap<>());
            sessionStarts.get(userId).put(chatId, Instant.now());
        }

        if (accessor.getCommand().equals(StompCommand.UNSUBSCRIBE)) {
            var userId = getUserId();
            var chatId = Long.parseLong(accessor.getDestination().split("/")[3]);
            sendStatisticsForChat(userId, chatId);
            sessionStarts.get(userId).remove(chatId);
        }

        if (accessor.getCommand().equals(StompCommand.DISCONNECT)) {
            var userId = sessionIdToUserId.get(accessor.getSessionId());
            for (var chatId: sessionStarts.get(userId).keySet()) {
                sendStatisticsForChat(userId, chatId);
                sessionStarts.get(userId).remove(chatId);
            }
        }

        if (
                accessor.getCommand().equals(StompCommand.MESSAGE)
                || accessor.getCommand().equals(StompCommand.SEND)
        ) {
            var userId = getUserId();
            sessionIdToUserId.put(accessor.getSessionId(), userId);
            var chatId = ((MessageCreateDto) converter.fromMessage(message, MessageCreateDto.class)).getChatId();
            sendStatistics(
                    new MessageStatisticsCreateDto(
                            userId,
                            chatId
                    )
            );
        }

        return message;
    }

    private static Long getUserId() {
        var userAuthContext = (UserAuthContext) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        if (userAuthContext == null) {
            throw new IllegalStateException("Пользователь не авторизован");
        }
        var userId = userAuthContext.getUser().getId();
        return userId;
    }

    private void sendStatisticsForChat(Long userId, Long chatId) {
        var sessionStart = sessionStarts.get(userId).get(chatId);
        sendStatistics(
                new ChatTimeStatisticsCreateDto(
                        chatId,
                        userId,
                        Instant.now().toEpochMilli()
                        - sessionStart.toEpochMilli()
                )
        );
    }

    private void sendStatistics(ChatTimeStatisticsCreateDto dto) {
        try {
            statisticsClient
                    .post()
                    .uri("/api/v1/chat-time")
                    .body(BodyInserters.fromValue(dto))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            logger.warn(
                    "Не удалось отправить статистику по пользователю {}, чат {}",
                    dto.getUserId(),
                    dto.getChatId()
            );
        }
    }

    private void sendStatistics(MessageStatisticsCreateDto dto) {
        try {
            statisticsClient
                    .post()
                    .uri("/api/v1/messages")
                    .body(BodyInserters.fromValue(dto))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            logger.warn(
                    "Не удалось отправить статистику по пользователю {}, чат {}",
                    dto.getUserId(),
                    dto.getChatId()
            );
        }
    }
}
