package com.xxavierr404.profile.dao;

import com.xxavierr404.profile.domain.ChatTimeStatistics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ChatTimeStatisticsRepository extends CrudRepository<ChatTimeStatistics, Long> {
    List<ChatTimeStatistics> findByUserIdAndChatIdAndCreationTimeBetween(
            Long userId,
            Long chatId,
            Instant start,
            Instant end
    );
}
