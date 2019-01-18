package io.renren.modules.knowledge.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.common.utils.R;
import io.renren.modules.knowledge.dao.KnowledgeQuestionDao;
import io.renren.modules.knowledge.entity.KnowledgeQuestionEntity;
import io.renren.modules.knowledge.service.KnowledgeQuestionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author hunji
 * @date 2018/12/4
 */
@Service("KnowledgeQuestionService")
public class KnowledgeQuestionServiceImpl extends ServiceImpl<KnowledgeQuestionDao,KnowledgeQuestionEntity> implements KnowledgeQuestionService {
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<KnowledgeQuestionEntity> page=this.selectPage(
                new Query<KnowledgeQuestionEntity>(params).getPage(),
                new EntityWrapper<>()
        );
        return new PageUtils(page);
    }

    @Override
    public void save(KnowledgeQuestionEntity entity) {
        this.insert(entity);
    }

    @Override
    public List<KnowledgeQuestionEntity> chatContent(Map<String, Object> params) {
        return baseMapper.chatContent(params);
    }

    @Override
    public List<KnowledgeQuestionEntity> chatContentPage(Map<String, Object> map) {
        return baseMapper.chatContentPage(map);
    }

    @Override
    public int chatContentPageCount(Map<String, Object> map) {
        return baseMapper.chatContentPageCount(map);
    }

}
