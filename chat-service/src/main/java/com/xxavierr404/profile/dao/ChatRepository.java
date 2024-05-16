package com.xxavierr404.profile.dao;

import com.xxavierr404.profile.domain.Chat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ChatRepository extends CrudRepository<Chat, Long> {
    List<Chat> findByIdIn(Set<Long> chatIds);
    List<Chat> findByOrganizationIdAndIsPublicTrue(Long organizationId);
}
