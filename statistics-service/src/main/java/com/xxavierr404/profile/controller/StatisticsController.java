package com.xxavierr404.profile.controller;

import com.xxavierr404.profile.dto.ChatTimeStatisticsCreateDto;
import com.xxavierr404.profile.dto.ChatTimeStatisticsResponseDto;
import com.xxavierr404.profile.dto.MessageStatisticsCreateDto;
import com.xxavierr404.profile.dto.MessageStatisticsResponseDto;
import com.xxavierr404.profile.security.UserAuthContext;
import com.xxavierr404.profile.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin
public class StatisticsController {
    private final StatisticsService statisticsService;

    @PostMapping("/chat-time")
    public ResponseEntity<Void> createStatisticsRecord(@RequestBody ChatTimeStatisticsCreateDto dto) {
        statisticsService.createStatistics(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/messages")
    public ResponseEntity<Void> createStatisticsRecord(@RequestBody MessageStatisticsCreateDto dto) {
        statisticsService.createStatistics(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/chat-time")
    public ResponseEntity<ChatTimeStatisticsResponseDto> getChatTimeStatistics(
            @RequestParam Long chatId,
            @RequestParam Long userId,
            @AuthenticationPrincipal UserAuthContext userAuthContext
    ) {
        return ResponseEntity.ok(
                statisticsService.getChatTimeStats(
                        chatId,
                        userId,
                        userAuthContext.getUser().getId()
                )
        );
    }

    @GetMapping("/messages")
    public ResponseEntity<MessageStatisticsResponseDto> getMessageStatistics(
            @RequestParam Long chatId,
            @RequestParam Long userId,
            @AuthenticationPrincipal UserAuthContext userAuthContext
    ) {
        return ResponseEntity.ok(
                statisticsService.getMessageStats(
                        chatId,
                        userId,
                        userAuthContext.getUser().getId()
                )
        );
    }
}
