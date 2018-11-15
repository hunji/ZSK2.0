package io.renren.modules.knowledge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hunji
 * @date 2018/11/5
 */
@Data
/**
 * 知识内容DTO
 */
public class ContentDTO implements Serializable {
    private static final long serialVersionUID = 1193048580385855089L;
    /**
     * id
     */
    private Long id;

    /**
     * 类型编号
     */
    private Long typeId;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 标题
     */
    private String title;
    /**
     * 简要描述
     */
    private String brief;
    /**
     * 详情 这里传输详情的话会导致加载变慢；在es中使用时才传content
     */
    private String content;

    /**
     * 0.未审核 1.已审核
     */
    private Integer state;

    /**
     * 提交人
     */
    private Long userId;
    /**
     * 提交人名称
     */
    private String userName;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 审核时间
     */
    private Date reviewDate;
    /**
     * 重要程度--评级时候判断
     * 分1-5级别;1级最高
     */
    private Integer rank;
    /**
     * 点赞数目
     */
    private Long likeNum;

    /**
     * 浏览数目
     */
    private Long viewNum;
}