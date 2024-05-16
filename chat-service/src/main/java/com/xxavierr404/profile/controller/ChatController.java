package com.xxavierr404.profile.controller;

import com.xxavierr404.profile.domain.Chat;
import com.xxavierr404.profile.dto.ChatCreateDto;
import com.xxavierr404.profile.dto.ChatResponseDto;
import com.xxavierr404.profile.dto.ChatUpdateDto;
import com.xxavierr404.profile.dto.UserInfoResponseDto;
import com.xxavierr404.profile.security.UserAuthContext;
import com.xxavierr404.profile.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/chats")
    public ResponseEntity<List<Chat>> getChatsOfUser(
            @AuthenticationPrincipal UserAuthContext authContext
    ) {
        return ResponseEntity.ok(
                chatService.getChatsContainingUser(authContext.getUser().getId())
        );
    }

    @GetMapping("/chats/available")
    public ResponseEntity<List<Chat>> getAvailableChatsOfUser(
            @AuthenticationPrincipal UserAuthContext authContext
    ) {
        return ResponseEntity.ok(
                chatService.getPublicChats(
                        authContext.getUser().getOrganizationId(),
                        authContext.getUser().getId()
                )
        );
    }

    @PostMapping("/chat")
    public ResponseEntity<Long> createChat(
            @RequestBody ChatCreateDto dto,
            @AuthenticationPrincipal UserAuthContext authContext
    ) {
        return ResponseEntity.status(
                HttpStatus.CREATED
        )
                .body(
                    chatService.createChat(
                            dto,
                            authContext.getUser().getId()
                    )
                );
    }

    @GetMapping("/chat/{id}")
    public ResponseEntity<ChatResponseDto> getChat(
            @PathVariable Long id,
            @AuthenticationPrincipal UserAuthContext authContext
    ) {
        return ResponseEntity.ok(chatService.getChat(
                authContext.getUser().getId(),
                id
        ));
    }

    @GetMapping("/chat/{chatId}/check-presence")
    public ResponseEntity<Boolean> addMember(
            @PathVariable Long chatId,
            @AuthenticationPrincipal UserAuthContext userAuthContext
    ) {
        return ResponseEntity.ok(
                chatService.isUserInChat(
                        userAuthContext.getUser().getId(),
                        chatId
                )
        );
    }

    @PutMapping("/chat/{id}")
    public ResponseEntity<Void> updateChat(
            @RequestBody ChatUpdateDto dto,
            @PathVariable Long id,
            @AuthenticationPrincipal UserAuthContext authContext
    ) {
        chatService.updateChat(
                dto,
                id,
                authContext.getUser().getId()
        );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/chat/{id}")
    public ResponseEntity<Void> deleteChat(
            @PathVariable Long id,
            @AuthenticationPrincipal UserAuthContext authContext
    ) {
        chatService.deleteChat(
                id,
                authContext.getUser().getId()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/chat/{chatId}/{userId}/check-rights")
    public ResponseEntity<Boolean> checkManagerRights(
            @PathVariable Long chatId,
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                chatService.userHasManagerRights(userId, chatId)
        );
    }

    @GetMapping("/chat/{chatId}/potential-members")
    public ResponseEntity<List<UserInfoResponseDto>> getPotentialMembers(
            @PathVariable Long chatId
    ) {
        return ResponseEntity.ok(
                chatService.getPotentialMembers(chatId)
        );
    }

    @PostMapping("/chat/{chatId}/{userId}")
    public ResponseEntity<Void> addMember(
            @PathVariable Long chatId,
            @PathVariable Long userId,
            @RequestParam Boolean isManager,
            @AuthenticationPrincipal UserAuthContext authContext
    ) {
        chatService.addMember(
                chatId,
                authContext.getUser().getId(),
                userId,
                isManager
        );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/chat/{chatId}/{userId}")
    public ResponseEntity<Void> addMember(
            @PathVariable Long chatId,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserAuthContext authContext
    ) {
        chatService.kickMember(
                chatId,
                authContext.getUser().getId(),
                userId
        );
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<String> errorHandler(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity<String> errorHandler(IllegalStateException exception) {
        return ResponseEntity.internalServerError().body(exception.getMessage());
    }
}
