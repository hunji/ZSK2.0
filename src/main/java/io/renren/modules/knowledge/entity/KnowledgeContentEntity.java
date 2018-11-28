package io.renren.modules.knowledge.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hunji
 * @date 2018/11/5
 * 知识库--知识内容实体
 */
@Data
@TableName("k_content")
@KeySequence("SEQ_K_CONTENT")
public class KnowledgeContentEntity  implements Serializable {
    private static final long serialVersionUID = -1410979333672295512L;

    @TableId(value="ID",type=IdType.INPUT)
    private Long id;

    /**
     * 类型 编号
     */
    private Long typeId;

    /**
     * 标题
     */
    private String title;
    /**
     * 简要描述
     */
    private String brief;
    /**
     * 详情
     */
    private String content;

    /**
     * 0.未审核 1.已审核
     */
    private Integer rstate;

    /**
     * 提交人
     */
    private Long userId;

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
