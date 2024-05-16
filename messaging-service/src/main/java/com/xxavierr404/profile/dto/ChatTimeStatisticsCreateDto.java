package com.xxavierr404.profile.dto;

import lombok.Value;

@Value
public class ChatTimeStatisticsCreateDto {
    Long chatId;
    Long userId;
    Long onlineTimeMillis;
}
