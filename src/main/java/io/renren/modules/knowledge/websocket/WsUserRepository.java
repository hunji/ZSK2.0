package io.renren.modules.knowledge.websocket;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import static io.renren.common.utils.Constant.KNOWLEDGE_GUEST_ROEL_NAME;

/**
 * @author hunji
 * @date 2019/1/21
 */
@Component
@Data
public class WsUserRepository {
    public static CopyOnWriteArraySet<String> guestUsers = new CopyOnWriteArraySet<>();
    public static CopyOnWriteArraySet<String> adminUsers = new CopyOnWriteArraySet<>();

    /**
     * 判断是否是guest用户
     * 有两种：一种是数据库中无记录的用户；一种是角色为游客的
     * @param roleNames
     * @return
     */
    public static boolean isGuest(List<String> roleNames){
        if(roleNames.size()==0 || roleNames.contains(KNOWLEDGE_GUEST_ROEL_NAME)){
            return true;
        }else{
            return false;
        }
    }
}
