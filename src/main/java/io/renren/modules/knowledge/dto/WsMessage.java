package io.renren.modules.knowledge.dto;

import io.renren.common.utils.Constant;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author hunji
 * @date 2018/12/5
 * websocket交互信息的封装类
 */
@Data
public class WsMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 交互信息的种类
     * 0.新用户登录
     * 1.用户退出
     * 2.发起问题
     * 3.回答问题
     */
    private Constant.MessageType type;

    private String sid;

    private String content;

    /**
     * 创建人
     */
    private String userName;

    /**
     * 创建时间
     */
    private Date createDate;
}
