package com.xxavierr404.profile.configuration;

import com.xxavierr404.profile.filter.JwtFilter;
import com.xxavierr404.profile.security.SecurityRuleChain;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfiguration {
    @Bean
    public JwtFilter jwtFilter(
            @Value("${jwt.secret.access}") String jwtSecret,
            SecurityRuleChain securityRuleChain
    ) {
        return new JwtFilter(jwtSecret, securityRuleChain);
    }

    @Bean
    public FilterRegistrationBean registration(@Qualifier("jwtFilter") Filter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity security,
            JwtFilter jwtFilter
    ) throws Exception {
        return security
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public SecurityRuleChain securityRuleChain() {
        return new SecurityRuleChain(List.of());
    }
}
