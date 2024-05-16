package com.xxavierr404.profile.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketsMessageBrokerConfiguration implements WebSocketMessageBrokerConfigurer {
    private final ChannelInterceptor jsonAuthInterceptor;
    private final ChannelInterceptor statisticsInterceptor;

    public WebSocketsMessageBrokerConfiguration(
            @Qualifier("jsonAuthInterceptor") ChannelInterceptor jsonAuthInterceptor,
            @Qualifier("statisticsMessageInterceptor") ChannelInterceptor statisticsInterceptor
    ) {
        this.jsonAuthInterceptor = jsonAuthInterceptor;
        this.statisticsInterceptor = statisticsInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/api/v1/messaging")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration
                .interceptors(jsonAuthInterceptor, statisticsInterceptor);
    }
}
