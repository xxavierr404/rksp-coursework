package com.xxavierr404.auth.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {
    @Bean
    @Qualifier("profilesWebClient")
    public WebClient profilesWebClient() {
        return WebClient.create("http://profiles:8080");
    }
}
