package io.renren.modules.knowledge.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.modules.knowledge.dao.KnowledgeHistoryDao;
import io.renren.modules.knowledge.entity.KnowledgeHistoryEntity;
import io.renren.modules.knowledge.service.KnowledgeHistoryService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author hunji
 * @date 2018/11/29
 */
@Service("knowledgeHistoryService")
public class KnowledgeHistoryServiceImpl extends ServiceImpl<KnowledgeHistoryDao,KnowledgeHistoryEntity> implements KnowledgeHistoryService {
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<KnowledgeHistoryEntity> page=this.selectPage(
                new Query<KnowledgeHistoryEntity>(params).getPage(),
                new EntityWrapper<>()
        );
        return new PageUtils(page);
    }

    @Override
    public void save(KnowledgeHistoryEntity entity) {
        this.insert(entity);
    }
}
