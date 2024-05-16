package com.xxavierr404.profile.security.rules;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface SecurityRule {
    boolean isConditionsMet(Claims claims);
    Collection<GrantedAuthority> onSuccess();
    Collection<GrantedAuthority> onFailure();
}
