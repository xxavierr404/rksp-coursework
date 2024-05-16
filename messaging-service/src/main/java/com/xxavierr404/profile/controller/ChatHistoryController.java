package com.xxavierr404.profile.controller;

import com.xxavierr404.profile.dto.MessageResponseDto;
import com.xxavierr404.profile.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/messages")
public class ChatHistoryController {
    private final MessageService messageService;

    @GetMapping("/history/{id}")
    public List<MessageResponseDto> subscribe(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        return messageService.readMessages(
                id,
                token.substring(7)
        );
    }
}
