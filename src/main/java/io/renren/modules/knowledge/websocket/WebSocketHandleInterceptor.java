package io.renren.modules.knowledge.websocket;

import com.sun.security.auth.UserPrincipal;
import org.apache.commons.lang.StringUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.security.Principal;

/**
 * @author hunji
 * @date 2019/1/18
 */
public class WebSocketHandleInterceptor extends ChannelInterceptorAdapter {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String username = accessor.getFirstNativeHeader("username");
            if (StringUtils.isEmpty(username)) {
                return null;
            }
            if(StompCommand.CONNECT.equals(accessor.getCommand())){
                // 绑定user
                Principal principal = new UserPrincipal(username);
                accessor.setUser(principal);
            }
        }
        return message;
    }

}
