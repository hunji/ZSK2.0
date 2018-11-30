package io.renren.modules.knowledge.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import io.renren.modules.knowledge.entity.KnowledgeHistoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hunji
 * @date 2018/11/29
 */
@Mapper
public interface KnowledgeHistoryDao extends BaseMapper<KnowledgeHistoryEntity> {
}
