package com.xxavierr404.profile.service;

import com.xxavierr404.profile.dao.ChatMemberRepository;
import com.xxavierr404.profile.dao.ChatRepository;
import com.xxavierr404.profile.domain.Chat;
import com.xxavierr404.profile.domain.ChatMember;
import com.xxavierr404.profile.domain.ChatMemberCompositeKey;
import com.xxavierr404.profile.domain.Role;
import com.xxavierr404.profile.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final WebClient usersWebClient;

    public List<Chat> getChatsContainingUser(Long userId) {
        var memberRecords = chatMemberRepository.findByUserId(userId);
        return chatRepository.findByIdIn(
                memberRecords.stream()
                        .map(ChatMember::getChatId)
                        .collect(Collectors.toSet())
        );
    }

    public List<Chat> getPublicChats(
            Long organizationId,
            Long userId
    ) {
        return chatRepository.findByOrganizationIdAndIsPublicTrue(organizationId)
                .stream()
                .filter(it -> chatMemberRepository.findById(
                            new ChatMemberCompositeKey(
                                    userId,
                                    it.getId()
                            )
                        ).isEmpty()
                )
                .toList();
    }

    public ChatResponseDto getChat(Long userId, Long chatId) {
        var memberRecord = chatMemberRepository.findById(
                new ChatMemberCompositeKey(
                      userId,
                      chatId
                )
        );
        if (memberRecord.isEmpty()) {
            throw new IllegalArgumentException("Пользователя нет в данном чате");
        }
        var chat =  chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Чат не найден"));
        var members = chatMemberRepository.findByChatId(chatId);
        return new ChatResponseDto(
                chat.getName(),
                chat.getDescription(),
                members.stream()
                        .map(it -> new ChatMemberResponseDto(
                                it.getUserId()
                        ))
                        .toList()
        );
    }

    @Transactional
    public Long createChat(ChatCreateDto dto, Long userId) {
        var userInfo = getUserInfo(userId);
        var chat = Chat.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .organizationId(
                        userInfo.getRole().equals(Role.EMPLOYEE)
                                ? userInfo.getOrganizationId()
                                : userInfo.getId()
                        )
                .isPublic(dto.getIsPublic())
                .build();
        var savedChat = chatRepository.save(chat);
        chatMemberRepository.save(
                new ChatMember(
                        userId,
                        savedChat.getId(),
                        Instant.now(),
                        true
                )
        );
        if (!chat.getOrganizationId().equals(userId)) {
            chatMemberRepository.save(
                    new ChatMember(
                            chat.getOrganizationId(),
                            savedChat.getId(),
                            Instant.now(),
                            true
                    )
            );
        }
        return savedChat.getId();
    }

    @Transactional
    public void updateChat(ChatUpdateDto dto, Long chatId, Long userId) {
        validateRights(userId, chatId);
        var oldChat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Чат не найден"));
        var chat = Chat.builder()
                .id(chatId)
                .name(dto.getName())
                .description(dto.getDescription())
                .isPublic(oldChat.getIsPublic())
                .organizationId(oldChat.getOrganizationId())
                .build();
        chatRepository.save(chat);
    }

    @Transactional
    public void deleteChat(Long chatId, Long userId) {
        validateRights(userId, chatId);
        chatMemberRepository.deleteByChatId(chatId);
        chatRepository.deleteById(chatId);
    }

    @Transactional
    public void addMember(
            Long chatId,
            Long managerId,
            Long newMemberId,
            Boolean isManager
    ) {
        var chat = chatRepository.findById(chatId).orElseThrow();
        if (!chat.getIsPublic()) {
            validateRights(managerId, chatId);
        } else {
            isManager = false;
        }

        var userInfo = getUserInfo(newMemberId);
        if (isUserInChat(newMemberId, chatId)) {
            throw new IllegalStateException("Пользователь уже есть в чате");
        }
        if (!userInfo.getOrganizationId().equals(chat.getOrganizationId())) {
            throw new IllegalStateException("Организация чата не соответствует организации пользователя");
        }

        var newMember = new ChatMember(
                newMemberId,
                chatId,
                Instant.now(),
                isManager
        );
        chatMemberRepository.save(newMember);
    }

    @Transactional
    public void kickMember(
            Long chatId,
            Long managerId,
            Long kickedMemberId
    ) {
        validateRights(managerId, chatId);
        if (!isUserInChat(kickedMemberId, chatId)) {
            throw new IllegalStateException("Пользователя нет в чате");
        }
        chatMemberRepository.deleteById(
                new ChatMemberCompositeKey(
                        kickedMemberId,
                        chatId
                )
        );
    }

    private void validateRights(Long userId, Long chatId) {
        if (!userHasManagerRights(userId, chatId)) {
            throw new IllegalStateException("У пользователя нет прав менеджера");
        }
    }

    public boolean userHasManagerRights(Long userId, Long chatId) {
        var member = chatMemberRepository.findById(
                new ChatMemberCompositeKey(
                        userId,
                        chatId
                )
        );
        return member.isPresent() && member.get().getIsManager();
    }

    private UserInfoResponseDto getUserInfo(Long userId) {
        return usersWebClient
                .get()
                .uri("/api/v1/user-info/%d".formatted(userId))
                .retrieve()
                .onStatus(
                        status -> !status.is2xxSuccessful(),
                        response -> Mono.error(new IllegalStateException(
                                "Не удалось проверить существование пользователя: %s".formatted(
                                        response.statusCode()
                                ))
                        )
                )
                .bodyToMono(UserInfoResponseDto.class)
                .block();
    }

    public List<UserInfoResponseDto> getPotentialMembers(Long chatId) {
        var chat = chatRepository.findById(chatId).orElseThrow();
        return getCompanyUserInfos(chat.getOrganizationId()).stream()
                .filter(it -> chatMemberRepository.findById(
                        new ChatMemberCompositeKey(
                                it.getId(),
                                chatId
                        )
                ).isEmpty())
                .toList();
    }

    private List<UserInfoResponseDto> getCompanyUserInfos(Long organizationId) {
        return usersWebClient
                .get()
                .uri("/api/v1/user-infos/organization/%d".formatted(organizationId))
                .retrieve()
                .onStatus(
                        status -> !status.is2xxSuccessful(),
                        response -> Mono.error(new IllegalStateException(
                                "Не удалось проверить существование пользователя: %s".formatted(
                                        response.statusCode()
                                ))
                        )
                )
                .bodyToFlux(UserInfoResponseDto.class)
                .toStream()
                .toList();
    }

    public boolean isUserInChat(Long userId, Long chatId) {
        return chatMemberRepository.findById(
                new ChatMemberCompositeKey(
                        userId,
                        chatId
                )
        ).isPresent();
    }
}
