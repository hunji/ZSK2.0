package io.renren.modules.knowledge.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * @author hunji
 * @date 2018/12/10
 * 这个类封装了用户 及是否在线的状态
 * 1.这里的用户包括了所有有过通话记录的
 *
 */
@Data
public class UserInfo implements Serializable {
    private String userName;
    private Boolean isOnline;
    private Boolean hasNewInfo;

    public UserInfo(String userName){
        this.userName=userName;
        this.isOnline=false;
        this.hasNewInfo=false;
    }
}
