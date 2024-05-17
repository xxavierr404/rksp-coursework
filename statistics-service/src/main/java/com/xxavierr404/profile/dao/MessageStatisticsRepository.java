package com.xxavierr404.profile.dao;

import com.xxavierr404.profile.domain.MessageStatistics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MessageStatisticsRepository extends CrudRepository<MessageStatistics, Long> {
    List<MessageStatistics> findByUserIdAndChatIdAndSendingTimeBetween(
            Long userId,
            Long chatId,
            Instant start,
            Instant end
    );
}
