package io.renren.modules.knowledge.websocket;

import com.google.gson.Gson;
import io.renren.common.utils.Constant;
import io.renren.modules.knowledge.dto.WsMessage;
import io.renren.modules.sys.service.SysUserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;

import static io.renren.common.utils.Constant.KNOWLEDGE_GUEST_ROEL_NAME;

/**
 * @author hunji
 * @date 2019/1/18
 */
@Component
public class WebSocketEventListener {
    private static final Logger log = LoggerFactory.getLogger(WebSocketEventListener.class);
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private SysUserRoleService userRoleService;
    private Gson gson=new Gson();
    /**
     * TODO: 处理新增连接和关闭连接事件；对其他用户进行提醒
     * @param event
     */
    @EventListener
    public void handleConnectListener(SessionConnectedEvent event) {
        String userName = StompHeaderAccessor.wrap(event.getMessage()).getUser().getName();
        List<String> roleNames = userRoleService.queryRoleByUserName(userName);

        // 有新用户连接后做两件事情：1.内存中增加实体；2.将消息通知给前台（admin通知给guest；guest通知给admin）
        boolean isAdded;
        boolean isGuest = false;
        if(roleNames.contains(KNOWLEDGE_GUEST_ROEL_NAME)){
            isAdded=WsUserRepository.guestUsers.add(userName);
            isGuest=true;
        }else{
            isAdded=WsUserRepository.adminUsers.add(userName);
        }
        if(isAdded){
            // 判断是否是游客登录，游客登录的话给后台角色是管理员的进行通知
            WsMessage message = new WsMessage();
            message.setContent("有新的用户登录:"+userName);
            message.setType(Constant.MessageType.ADDUSER);
            message.setSid(userName);
            sendMessage(message,isGuest);
        }
    }

    @EventListener
    public void handleDisconnectListener(SessionDisconnectEvent event) {
        String userName = StompHeaderAccessor.wrap(event.getMessage()).getUser().getName();
        List<String> roleNames = userRoleService.queryRoleByUserName(userName);
        boolean isDeleted;
        boolean isGuest = false;
        if(roleNames.contains(KNOWLEDGE_GUEST_ROEL_NAME)){
            isDeleted=WsUserRepository.guestUsers.remove(userName);
            isGuest=true;
        }else{
            isDeleted=WsUserRepository.adminUsers.remove(userName);
        }
        if(isDeleted){
            WsMessage message = new WsMessage();
            message.setContent("有新的用户退出登录:"+userName);
            message.setType(Constant.MessageType.QUIT);
            message.setSid(userName);
            sendMessage(message,isGuest);
        }
    }

    /**
     * 发送消息
     * @param message 消息内容
     * @param isGuest 是否是游客发的消息
     */
    private void sendMessage(WsMessage message,boolean isGuest){
        String jsonMessage = gson.toJson(message);
        if(isGuest){
            WsUserRepository.adminUsers.forEach(user->
                    messagingTemplate.convertAndSendToUser(user,"/chat/login",jsonMessage));
        }else{
            WsUserRepository.guestUsers.forEach(user->
                    messagingTemplate.convertAndSendToUser(user,"/chat/login",jsonMessage));
        }
    }
}
