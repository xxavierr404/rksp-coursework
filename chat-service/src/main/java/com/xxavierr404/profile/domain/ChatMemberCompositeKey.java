package com.xxavierr404.profile.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberCompositeKey implements Serializable {
    Long userId;
    Long chatId;
}
