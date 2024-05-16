package com.xxavierr404.profile.configuration;

import com.xxavierr404.profile.filter.JwtFilter;
import com.xxavierr404.profile.security.SecurityRuleChain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity security,
            @Value("${jwt.secret.access}") String jwtSecret,
            SecurityRuleChain securityRuleChain
    ) throws Exception {
        return security
                .authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, "/api/v1/chat-time").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, "/api/v1/messages").permitAll())
                .addFilterBefore(
                        new JwtFilter(jwtSecret, securityRuleChain),
                        UsernamePasswordAuthenticationFilter.class
                )
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public SecurityRuleChain securityRuleChain() {
        return new SecurityRuleChain(List.of());
    }
}
