package com.xxavierr404.profile.filter;

import com.xxavierr404.profile.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.context.SecurityContextHolder;

@RequiredArgsConstructor
public class JwtTokenAuthInterceptor implements ChannelInterceptor {
    private final JwtFilter jwtFilter;
    private final MessageService messageService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand().equals(StompCommand.DISCONNECT)) {
            return message;
        }

        var tokenList = accessor.getNativeHeader("X-Authorization");
        if (tokenList == null || tokenList.isEmpty() || tokenList.get(0) == null) {
            throw new IllegalStateException("Не найден токен аутентификации");
        }

        var token = tokenList.get(0);
        var authContext = jwtFilter.parseToken(token.substring(7));
        if (authContext == null) {
            throw new IllegalArgumentException("Ошибка авторизации");
        }
        if (accessor.getCommand().equals(StompCommand.SUBSCRIBE)) {
            var chatId = Long.parseLong(accessor.getDestination().split("/")[3]);
            messageService.validateUserInChat(chatId, token.substring(7));
        }
        SecurityContextHolder.getContext().setAuthentication(authContext);
        accessor.setUser(authContext);
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }
}
