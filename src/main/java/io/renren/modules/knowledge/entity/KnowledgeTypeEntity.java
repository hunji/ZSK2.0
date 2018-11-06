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

/**
 * @author hunji
 * @date 2018/11/5
 * 知识库--知识类型实体
 */
@Data
@TableName("k_type")
@KeySequence("SEQ_K_CONTENT_TYPE")
public class KnowledgeTypeEntity implements Serializable {
    private static final long serialVersionUID = 1588779192020639364L;

    @TableId(value="ID",type=IdType.INPUT)
    private Long id;

    /**
     * 类型名称--可以进行动态增加修改删除
     */
    @NotBlank(message="类型名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String typeName;

    /**
     * 类型描述
     */
    private String description;

}

