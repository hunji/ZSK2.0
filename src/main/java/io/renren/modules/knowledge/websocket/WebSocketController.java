package io.renren.modules.knowledge.websocket;

import com.google.gson.Gson;
import io.renren.modules.knowledge.dto.WsMessage;
import io.renren.modules.knowledge.entity.KnowledgeQuestionEntity;
import io.renren.modules.sys.service.SysUserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import static io.renren.common.utils.Constant.KNOWLEDGE_GUEST_ROEL_NAME;

/**
 * @author hunji
 * @date 2019/1/18
 */
@Controller
public class WebSocketController {
    private static final Logger log = LoggerFactory.getLogger(WebSocketEventListener.class);
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private SysUserRoleService userRoleService;
    private Gson gson = new Gson();

    @MessageMapping("/hello")
    public void chat(WsMessage message) throws Exception {
        // 收到websocket请求 并且转发给相应的前台
        System.out.println("后台收到请求：" + message);

        List<String> roleNames = userRoleService.queryRoleByUserName(message.getUserName());
        String toSID = message.getSid();
        if (WsUserRepository.isGuest(roleNames)) {
            // 用户在线时实时推送，不在线的话不处理
            CopyOnWriteArraySet<String> adminUsers = WsUserRepository.adminUsers;
            for (String user : adminUsers) {
                if (user.equals(toSID)) {
                    messagingTemplate.convertAndSendToUser(user, "/chat/login", message);
                }
            }
        }else{
            CopyOnWriteArraySet<String> guestUsers = WsUserRepository.guestUsers;
            for (String user:guestUsers) {
                if (user.equals(toSID)) {
                    messagingTemplate.convertAndSendToUser(user, "/chat/login", message);
                }
            }
        }
    }


}
