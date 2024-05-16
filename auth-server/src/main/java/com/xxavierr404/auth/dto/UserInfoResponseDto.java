package com.xxavierr404.auth.dto;

import com.xxavierr404.auth.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoResponseDto {
    Long id;
    String firstName;
    String lastName;
    Long organizationId;
    Role role;
}
