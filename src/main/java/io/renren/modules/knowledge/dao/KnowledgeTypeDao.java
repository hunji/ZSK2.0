package io.renren.modules.knowledge.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import io.renren.modules.knowledge.entity.KnowledgeTypeEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hunji
 * @date 2018/11/5
 */
@Mapper
public interface KnowledgeTypeDao  extends BaseMapper<KnowledgeTypeEntity> {
}

