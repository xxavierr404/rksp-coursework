package com.xxavierr404.profile.configuration;

import com.xxavierr404.profile.filter.JwtFilter;
import com.xxavierr404.profile.filter.JwtTokenAuthInterceptor;
import com.xxavierr404.profile.filter.StatisticsMessageInterceptor;
import com.xxavierr404.profile.service.MessageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebSocketsSecurityConfiguration extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.anyMessage().permitAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Bean
    public JwtTokenAuthInterceptor jsonAuthInterceptor(
            JwtFilter jwtFilter,
            MessageService messageService
    ) {
        return new JwtTokenAuthInterceptor(
                jwtFilter,
                messageService
        );
    }

    @Bean
    public StatisticsMessageInterceptor statisticsMessageInterceptor(
            @Qualifier("statisticsWebClient") WebClient statisticsWebClient,
            @Lazy CompositeMessageConverter compositeMessageConverter
    ) {
        return new StatisticsMessageInterceptor(
                statisticsWebClient,
                compositeMessageConverter
        );
    }
}
