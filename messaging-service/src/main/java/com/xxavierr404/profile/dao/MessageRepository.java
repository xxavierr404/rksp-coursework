package com.xxavierr404.profile.dao;

import com.xxavierr404.profile.domain.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {
    List<Message> findByChatId(Long chatId);
}
