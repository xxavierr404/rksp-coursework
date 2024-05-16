package com.xxavierr404.profile.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregatedMessageStatistics {
    Long totalMessageCount;
    Double meanMessagesCountPerDay;
    Integer mostActiveDay;
}
