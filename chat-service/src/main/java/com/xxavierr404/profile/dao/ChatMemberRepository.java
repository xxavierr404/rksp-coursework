package com.xxavierr404.profile.dao;

import com.xxavierr404.profile.domain.ChatMember;
import com.xxavierr404.profile.domain.ChatMemberCompositeKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMemberRepository extends CrudRepository<ChatMember, ChatMemberCompositeKey> {
    List<ChatMember> findByUserId(Long userId);
    List<ChatMember> findByChatId(Long chatId);
    void deleteByChatId(Long chatId);
}
