package io.renren.modules.knowledge.websocket;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author hunji
 * @date 2019/1/21
 */
@Component
@Data
public class WsUserRepository {
    public static CopyOnWriteArraySet<String> guestUsers = new CopyOnWriteArraySet<>();
    public static CopyOnWriteArraySet<String> adminUsers = new CopyOnWriteArraySet<>();
}
