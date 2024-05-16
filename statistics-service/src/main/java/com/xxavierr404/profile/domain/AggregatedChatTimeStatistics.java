package com.xxavierr404.profile.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregatedChatTimeStatistics {
    Double mean_presence_time_per_visit_millis;
    Integer most_active_day;
}
