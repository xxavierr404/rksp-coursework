package com.xxavierr404.profile.dto;

import lombok.Value;

import java.time.DayOfWeek;

@Value
public class MessageStatisticsResponseDto {
    Long totalMessageCount;
    Double meanMessagesCountPerDay;
    DayOfWeek mostActiveDay;
}
