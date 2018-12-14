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
 * @date 2018/12/4
 */
@Data
@TableName("K_QUESTION")
@KeySequence("SEQ_K_QUESTION")
public class KnowledgeQuestionEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value="ID",type=IdType.INPUT)
    private Long id;

    /**
     * 内容
     */
    private String content;

    /**
     * 长连接SID
     */
    private String sid;

    /**
     * 创建人
     */
    private String userName;

    /**
     * 创建时间
     */
    private Date createDate;

}
