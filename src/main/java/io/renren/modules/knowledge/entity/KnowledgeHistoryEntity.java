package io.renren.modules.knowledge.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.renren.common.validator.group.AddGroup;
import io.renren.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @author hunji
 * @date 2018/11/29
 */
@Data
@TableName("K_HISTORY")
@KeySequence("SEQ_K_HISTORY")
public class KnowledgeHistoryEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value="HISTORY_ID",type=IdType.INPUT)
    private Long id;

    /**
     * 查询人名称
     */
    private String userName;

    /**
     * 查询时间
     */
    private Date useDate;

    /**
     * 查询内容
     */
    @NotBlank(message="查询内容不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String useContent;

    /**
     * 流程实例id
     */
    @TableId(value="FLOWSN")
    private String flowsn;

    /**
     * 流程ID
     */
    @TableId(value="FLOW_ID")
    private String flowId;
    /**
     * 流程名称
     */
    private String flowName;
    /**
     * 环节名称
     */
    private String stepName;
    /**
     * 其他信息
     */
    private String otherInfo;

}
