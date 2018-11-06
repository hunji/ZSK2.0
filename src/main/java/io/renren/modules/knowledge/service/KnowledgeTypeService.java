package io.renren.modules.knowledge.service;

import com.baomidou.mybatisplus.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.knowledge.entity.KnowledgeTypeEntity;

import java.util.List;
import java.util.Map;

/**
 * @author hunji
 * @date 2018/11/5
 */
public interface KnowledgeTypeService extends IService<KnowledgeTypeEntity> {
    /**
     * 获取分页数据
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String,Object> params);

    /**
     * 获取所有类型数据
     * @return
     */
    List<KnowledgeTypeEntity> queryAll();
    /**
     * 保存类型
     *
     * @param knowledgeType
     */
    void save(KnowledgeTypeEntity knowledgeType);

    /**
     * 更新类型
     *
     * @param knowledgeType
     */
    void update(KnowledgeTypeEntity knowledgeType);

    /**
     * 批量删除类型
     *
     * @param ids
     */
    void deleteBatch(Long[] ids);

}
