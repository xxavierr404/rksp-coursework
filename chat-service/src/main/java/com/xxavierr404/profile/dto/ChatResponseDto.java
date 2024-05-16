package com.xxavierr404.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChatResponseDto {
    String name;
    String description;
    List<ChatMemberResponseDto> members;
}
