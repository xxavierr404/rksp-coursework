package com.xxavierr404.auth.dto;

import com.xxavierr404.auth.domain.Role;
import com.xxavierr404.auth.domain.User;
import lombok.Data;

@Data
public class UserRegisterDto {
    String login;
    String password;
    Role role;
    String firstName;
    String lastName;
    Long organizationId;

    public User toEntity() {
        return new User(
                null,
                login,
                password,
                firstName,
                lastName,
                role,
                organizationId
        );
    }
}
