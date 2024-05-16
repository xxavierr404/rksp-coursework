package com.xxavierr404.profile.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ChatMemberCompositeKey.class)
public class ChatMember {
    @Id
    Long userId;
    @Id
    Long chatId;
    Instant joinTime;
    Boolean isManager;
}
