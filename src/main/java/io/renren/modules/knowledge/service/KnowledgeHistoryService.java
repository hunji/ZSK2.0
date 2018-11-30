package io.renren.modules.knowledge.service;

import com.baomidou.mybatisplus.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.knowledge.entity.KnowledgeHistoryEntity;

import java.util.Map;

/**
 * @author hunji
 * @date 2018/11/29
 */
public interface KnowledgeHistoryService  extends IService<KnowledgeHistoryEntity> {
    /**
     * 分页查询
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String,Object> params);

    /**
     * 保存查询记录
     *
     * @param knowledgeType
     */
    void save(KnowledgeHistoryEntity entity);
}
