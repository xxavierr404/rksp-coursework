package com.xxavierr404.profile.dto;

import lombok.Value;

@Value
public class ChatCreateDto {
    String name;
    String description;
    Boolean isPublic;
}
