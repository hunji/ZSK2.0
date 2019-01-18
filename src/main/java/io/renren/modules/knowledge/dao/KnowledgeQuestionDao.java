package io.renren.modules.knowledge.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import io.renren.modules.knowledge.entity.KnowledgeQuestionEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author hunji
 * @date 2018/12/4
 */
@Mapper
public interface KnowledgeQuestionDao extends BaseMapper<KnowledgeQuestionEntity> {
    /**
     * 获取两人对话内容
     * @param userName
     * @param SID
     * @return
     */
    List<KnowledgeQuestionEntity> chatContent(Map<String, Object> params);

    List<KnowledgeQuestionEntity> chatContentPage(Map<String, Object> map);

    int chatContentPageCount(Map<String, Object> map);
}
