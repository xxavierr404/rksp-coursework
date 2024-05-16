package com.xxavierr404.profile.security;

import com.xxavierr404.profile.security.rules.SecurityRule;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
public class SecurityRuleChain {
    private final List<SecurityRule> rules;

    public Collection<? extends GrantedAuthority> getPermissions(Claims claims) {
        var authorities = new HashSet<GrantedAuthority>();
        for (var rule: rules) {
            authorities.addAll(
                    rule.isConditionsMet(claims)
                            ? rule.onSuccess()
                            : rule.onFailure()
            );
        }
        return authorities;
    }
}
