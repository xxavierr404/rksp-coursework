package com.xxavierr404.profile.service;

import com.xxavierr404.profile.dao.MessageRepository;
import com.xxavierr404.profile.domain.Message;
import com.xxavierr404.profile.dto.MessageCreateDto;
import com.xxavierr404.profile.dto.MessageResponseDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final WebClient chatsClient;

    public MessageService(
            MessageRepository messageRepository,
            @Qualifier("chatsWebClient") WebClient chatsClient
    ) {
        this.messageRepository = messageRepository;
        this.chatsClient = chatsClient;
    }

    @Transactional
    public MessageResponseDto createMessage(
            MessageCreateDto dto,
            Long userId,
            String token
    ) {
        validateUserInChat(dto.getChatId(), token);

        var message = messageRepository.save(
                new Message(
                        null,
                        userId,
                        dto.getChatId(),
                        dto.getText(),
                        Instant.now()
                )
        );
        return new MessageResponseDto(
                message.getAuthorId(),
                message.getChatId(),
                message.getText(),
                message.getSendingTime()
        );
    }

    public List<MessageResponseDto> readMessages(
            Long chatId,
            String token
    ) {
        validateUserInChat(chatId, token);
        return messageRepository.findByChatId(chatId)
                .stream()
                .map(it -> new MessageResponseDto(
                        it.getAuthorId(),
                        it.getChatId(),
                        it.getText(),
                        it.getSendingTime()
                ))
                .toList();
    }

    public void validateUserInChat(Long chatId, String token) {
        if (!isUserInChat(chatId, token)) {
            throw new IllegalArgumentException("Пользователь не состоит в чате");
        }
    }

    private boolean isUserInChat(
            Long chatId,
            String token
    ) {
        return Boolean.TRUE.equals(chatsClient
                .get()
                .uri(
                        "/api/v1/chat/%d/check-presence"
                                .formatted(chatId)
                )
                .header(
                        "Authorization",
                        "Bearer %s".formatted(token)
                )
                .retrieve()
                .onStatus(
                        status -> !status.is2xxSuccessful(),
                        response -> Mono.error(
                                new IllegalStateException("Не удалось проверить наличие в чате: %s".formatted(
                                        response.statusCode()
                                ))
                        )
                )
                .bodyToMono(Boolean.class)
                .block());
    }
}
