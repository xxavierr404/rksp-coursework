package com.xxavierr404.profile.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Role implements GrantedAuthority {
    ORGANIZATION("ORGANIZATION"),
    EMPLOYEE("EMPLOYEE");

    private final String value;

    @Override
    public String getAuthority() {
        return value;
    }
}
