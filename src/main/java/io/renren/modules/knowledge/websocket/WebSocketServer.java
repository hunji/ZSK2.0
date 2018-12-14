package io.renren.modules.knowledge.websocket;

import com.google.gson.Gson;
import io.renren.common.utils.Constant;
import io.renren.config.CustomSpringConfigurator;
import io.renren.modules.knowledge.dto.WsMessage;
import io.renren.modules.sys.service.SysUserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import static io.renren.common.utils.Constant.KNOWLEDGE_GUEST_ROEL_NAME;

/**
 * @author hunji
 * @date 2018/12/11
 */
@ServerEndpoint(value="/websocket/{sid}", configurator = CustomSpringConfigurator.class)
@Component
@Scope("prototype")
@Slf4j
public class WebSocketServer {
    @Autowired
    private SysUserRoleService userRoleService;
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的
     */
    public static int userOnlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象
     */
    public static CopyOnWriteArraySet<WebSocketServer> userWebSocketSet = new CopyOnWriteArraySet<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    public Session session;

    /**
     * 接收sid
     */
    public String sid="";
    public boolean isGuest=false;
    private Gson gson=new Gson();

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session,@PathParam("sid")String sid) throws IOException {
        // 前台传来用户名，把用户id视为sid进行通信
        this.sid=sid;
        this.session = session;
        List<String> roleNames = userRoleService.queryRoleByUserName(sid);
        if(roleNames.contains(KNOWLEDGE_GUEST_ROEL_NAME)){
            this.isGuest=true;
        }
        userOnlineCount++;
        userWebSocketSet.add(this);
        log.info("有新窗口开始监听:"+this.sid+",当前在线人数为" +userOnlineCount);

        // 判断是否是游客登录，游客登录的话给后台角色是管理员的进行通知
        WsMessage message = new WsMessage();
        message.setContent("有新的游客登录:"+sid);
        message.setType(Constant.MessageType.ADDUSER);
        message.setSid(sid);
        if(isGuest){
            sendInfoToAdmin(message);
        }else{
            sendInfoToGuest(message);
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() throws IOException {
        userOnlineCount--;
        userWebSocketSet.remove(this);
        log.info("有一连接关闭！当前在线人数为" + userOnlineCount);

        //判断是否是游客退出，游客登录的话给后台进行通知
        WsMessage message = new WsMessage();
        message.setContent("游客退出登录:"+sid);
        message.setType(Constant.MessageType.QUIT);
        message.setSid(sid);
        if(isGuest){
            sendInfoToAdmin(message);
        }else{
            sendInfoToGuest(message);
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message) throws IOException {
        WsMessage getMessage= gson.fromJson(message, WsMessage.class);
        Constant.MessageType type = getMessage.getType();

        if(type.equals(Constant.MessageType.CONTENT)){
            // 用户在线时实时推送，不在线的话不处理
            String toSID = getMessage.getSid();
            for (WebSocketServer webSocketServer : userWebSocketSet) {
                if(webSocketServer.sid.equals(toSID)){
                    webSocketServer.sendMessage(message);
                    return;
                }
            }
        }
    }

    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }
    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 给所有的管理员发消息
     * @param message
     */
    public void sendInfoToAdmin(WsMessage message) throws IOException {
        String jsonMessage = gson.toJson(message);
        for (WebSocketServer webSocketServer : userWebSocketSet) {
            if(!webSocketServer.isGuest){
                webSocketServer.sendMessage(jsonMessage);
            }
        }
    }
    /**
     * 给所有的guest发消息
     * @param message
     */
    public void sendInfoToGuest(WsMessage message) throws IOException {
        String jsonMessage = gson.toJson(message);
        for (WebSocketServer webSocketServer : userWebSocketSet) {
            if(webSocketServer.isGuest){
                webSocketServer.sendMessage(jsonMessage);
            }
        }
    }
}
