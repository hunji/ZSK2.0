package io.renren.modules.knowledge.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import io.renren.common.exception.RRException;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.modules.knowledge.dao.KnowledgeTypeDao;
import io.renren.modules.knowledge.entity.KnowledgeTypeEntity;
import io.renren.modules.knowledge.service.KnowledgeContentService;
import io.renren.modules.knowledge.service.KnowledgeTypeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author hunji
 * @date 2018/11/5
 */
@Service("knowledgeTypeService")
public class KnowledgeTypeServiceImpl extends ServiceImpl<KnowledgeTypeDao,KnowledgeTypeEntity> implements KnowledgeTypeService {
    @Autowired
    private KnowledgeContentService knowledgeContentService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String typeName=(String)params.get("typeName");

        Page<KnowledgeTypeEntity> page=this.selectPage(
                new Query<KnowledgeTypeEntity>(params).getPage(),
                new EntityWrapper<KnowledgeTypeEntity>()
                        .like(StringUtils.isNotBlank(typeName),"type_name",typeName)
        );
        return new PageUtils(page);
    }

    @Override
    public List<KnowledgeTypeEntity> queryAll() {
        List<KnowledgeTypeEntity> list=this.selectList(
                new EntityWrapper<KnowledgeTypeEntity>().orderBy("order_num")
        );
        return list;
    }

    @Override
    public List<KnowledgeTypeEntity> queryAllButTheme() {
        List<KnowledgeTypeEntity> list=this.selectList(
                new EntityWrapper<KnowledgeTypeEntity>().ne("type",0).orderBy("order_num")
        );
        return list;
    }

    @Override
    public void save(KnowledgeTypeEntity knowledgeType) {
        this.insert(knowledgeType);
    }

    @Override
    public void update(KnowledgeTypeEntity knowledgeType) {
        this.updateById(knowledgeType);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        int count=knowledgeContentService.getCountByType(ids);
        if(count>0){
            throw new RRException("当前类型下有"+count+"个知识内容，请先删除内容");
        }
        this.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public List<KnowledgeTypeEntity> queryListParentId(Long parentId) {
        List<KnowledgeTypeEntity> list = this.selectList(
                new EntityWrapper<KnowledgeTypeEntity>().eq("parent_id", parentId)
        );
        return list;
    }

    @Override
    public void deleteEntity(Long id) {
        int count=knowledgeContentService.getCountByType(new Long[]{id});
        if(count>0){
            throw new RRException("当前类型下有"+count+"个知识内容，请先删除内容");
        }
        this.deleteById(id);
    }

}
