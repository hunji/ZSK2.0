package io.renren.modules.knowledge.dto;

import io.renren.common.utils.PageUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * @author hunji
 * @date 2018/11/14
 */
@Data
public class ESContentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 查询检索获得的文档数量
     */
    private Long totalHits;
    /**
     * 检索花费的时间（毫秒为单位）
     */
    private Long took;

    /**
     * 返回分页数据
     */
    private PageUtils page;
}
