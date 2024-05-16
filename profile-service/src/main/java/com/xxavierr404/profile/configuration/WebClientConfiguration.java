package com.xxavierr404.profile.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {
    @Bean
    @Qualifier("authWebClient")
    public WebClient usersWebClient() {
        return WebClient.create("http://auth:8080");
    }
}
