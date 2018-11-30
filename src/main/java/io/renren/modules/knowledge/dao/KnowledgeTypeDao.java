package io.renren.modules.knowledge.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import io.renren.modules.knowledge.entity.KnowledgeTypeEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * @author hunji
 * @date 2018/11/5
 */
@Mapper
public interface KnowledgeTypeDao  extends BaseMapper<KnowledgeTypeEntity> {

    /**
     * 递归查询出某个类型下的全部子类型
     * 这里为了避免多次去查询数据库使用了oracle的
     * start with t1.id = #{type_id}
     * connect by t1.PARENT_ID= prior t1.ID;
     * @param typeID
     * @return
     */
    List<KnowledgeTypeEntity> queryByParent(Long typeID);
}

