package io.renren.modules.knowledge.dto;

import io.renren.common.utils.PageUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hunji
 * @date 2018/12/5
 */
@Data
public class QuestionDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 返回分页数据
     */
    private PageUtils page;

    private Integer totalCount;
}
