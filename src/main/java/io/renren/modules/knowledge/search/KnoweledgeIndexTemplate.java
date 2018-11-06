package io.renren.modules.knowledge.search;

import lombok.Data;

import java.util.Date;

/**
 * @author hunji
 * @date 2018/11/5
 * ES中的索引结构模板
 */
@Data
public class KnoweledgeIndexTemplate {
    private Long id;
    private String title;
    private String brief;
    private String content;
    private Integer likeNum;
    private Date reviewDate;
}
