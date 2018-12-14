package io.renren.modules.knowledge.service;

import com.baomidou.mybatisplus.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.knowledge.entity.KnowledgeQuestionEntity;

import java.util.List;
import java.util.Map;

/**
 * @author hunji
 * @date 2018/12/4
 */
public interface KnowledgeQuestionService extends IService<KnowledgeQuestionEntity> {
    PageUtils queryPage(Map<String,Object> params);
    void save(KnowledgeQuestionEntity entity);
    List<KnowledgeQuestionEntity> chatContent(Map<String, Object> params);
}
