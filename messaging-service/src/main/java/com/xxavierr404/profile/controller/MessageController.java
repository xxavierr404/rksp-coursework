package com.xxavierr404.profile.controller;

import com.xxavierr404.profile.dto.MessageCreateDto;
import com.xxavierr404.profile.dto.MessageResponseDto;
import com.xxavierr404.profile.security.UserAuthContext;
import com.xxavierr404.profile.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/messaging")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/send-message")
    public MessageResponseDto send(
            @RequestBody MessageCreateDto msg,
            @Header("X-Authorization") String token,
            @AuthenticationPrincipal UserAuthContext userAuthContext
    ) {
        var message = messageService.createMessage(
                msg,
                userAuthContext.getUser().getId(),
                token.substring(7)
        );
        messagingTemplate.convertAndSend(
                "/topic/chat/%d".formatted(msg.getChatId()),
                message
        );
        return message;
    }
}
