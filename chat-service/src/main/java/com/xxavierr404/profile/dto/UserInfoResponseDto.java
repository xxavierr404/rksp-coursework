package com.xxavierr404.profile.dto;

import com.xxavierr404.profile.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoResponseDto {
    Long id;
    Long organizationId;
    Role role;
}
