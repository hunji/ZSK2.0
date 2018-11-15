package io.renren.modules.knowledge.search;
import io.renren.modules.knowledge.dto.ESContentDTO;

import java.util.Map;

/**
 * 检索接口
 * @author hunji
 * @date 2018/11/5
 */
public interface ISearchService {

    /**
     * 索引目标知识内容
     * @param contentId
     */
    void index(Long contentId);

    /**
     * 移除知识索引
     */
    void remove(Long contentId);

    /**
     * 搜索引擎查询数据
     * @param params
     * @return
     */
    ESContentDTO queryIndex(Map<String, Object> params);

    /**
     * es部分字段更新
     * @param contentId
     * @param jsonMap 如Map<String, Object> jsonMap = new HashMap<>();
     * jsonMap.put("updated", new Date());
     */
    boolean updatePartial(String contentId,Map<String, Object> jsonMap);
}
