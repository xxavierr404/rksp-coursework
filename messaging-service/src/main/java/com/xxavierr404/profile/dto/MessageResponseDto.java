package com.xxavierr404.profile.dto;

import lombok.Value;

import java.time.Instant;

@Value
public class MessageResponseDto {
    Long authorId;
    Long chatId;
    String text;
    Instant sendingTime;
}
