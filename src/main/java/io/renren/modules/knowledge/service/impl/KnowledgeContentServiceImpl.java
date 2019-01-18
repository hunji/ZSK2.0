package io.renren.modules.knowledge.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.modules.knowledge.dao.KnowledgeContentDao;
import io.renren.modules.knowledge.dto.ContentDTO;
import io.renren.modules.knowledge.entity.KnowledgeContentEntity;
import io.renren.modules.knowledge.entity.KnowledgeTypeEntity;
import io.renren.modules.knowledge.search.ISearchService;
import io.renren.modules.knowledge.service.KnowledgeContentService;
import io.renren.modules.knowledge.service.KnowledgeTypeService;
import io.renren.modules.sys.service.ShiroService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static io.renren.common.utils.Constant.knowledgeContentPermission;

/**
 * @author hunji
 * @date 2018/11/5
 */
@Service("knowledgeContentService")
public class KnowledgeContentServiceImpl extends ServiceImpl<KnowledgeContentDao, KnowledgeContentEntity> implements KnowledgeContentService {
    @Autowired
    private ShiroService shiroService;
    @Autowired
    private ISearchService searchService;
    @Autowired
    private KnowledgeTypeService typeService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        //获取当前人自己的知识内容
        //具有审核权限的人可以看到其他人未审核的知识内容
        Page<KnowledgeContentEntity> page;

        Long userId = (Long) params.get("userId");

        Set<String> permissions = shiroService.getUserPermissions(userId);
        if (!permissions.contains(knowledgeContentPermission)) {
            params.put("user_id", userId);
        }
        params.put("rstate", 0);
        return this.queryDTO(params);
    }

    @Override
    public PageUtils commonContentPage(Map<String, Object> params) {
        // 改成DTO给页面使用
        params.put("rstate", 1);
        // 增加了根据类型查询；类型又分为了主题和类型两类
        // 类型通过递归查询先找到所有的子类,然后把子类
        String type_id = params.get("type_id").toString();
        if(StringUtils.isNotBlank(type_id) && !"0".equals(type_id.trim())){
            List<Long> typeIDs = typeService.queryListParentId(Long.parseLong(type_id))
                    .stream().map(t -> t.getId()).collect(Collectors.toList());
            params.put("typeIDs", typeIDs);
        }
        return this.queryDTO(params);
    }

    @Override
    public void save(KnowledgeContentEntity knowledgeContent) {
        this.insert(knowledgeContent);
    }

    @Override
    public void update(KnowledgeContentEntity knowledgeContent) {
        this.updateById(knowledgeContent);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        this.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public void review(Long id) {
        KnowledgeContentEntity entity = this.selectById(id);
        entity.setRstate(new Integer(1));
        entity.setReviewDate(new Date());
        this.updateById(entity);

        // 只有在通过审核时 构建索引到es上
        // 新增或者更新时并不进行更新索引，只有在通过审核后才去！！！
        searchService.index(id);
    }

    @Override
    public void sendback(Long id) {
        KnowledgeContentEntity entity = this.selectById(id);
        entity.setRstate(new Integer(0));
        this.updateById(entity);

        // 审核退回时 删除索引
        searchService.remove(id);
    }

    @Override
    public int getCountByType(Long[] typeIds) {

        int count = 0;
        for (Long typeId : typeIds) {
            count += this.selectCount(
                    new EntityWrapper<KnowledgeContentEntity>()
                            .eq("type_id", typeId)
            );
        }
        return count;
    }

    @Override
    public PageUtils queryDTO(Map<String, Object> map) {
        Query query = new Query(map);
        List<ContentDTO> dtoList = baseMapper.queryDTO(query);
        int total = baseMapper.queryDTOCount(query);

        PageUtils page = new PageUtils(dtoList, total, query.getLimit(), query.getCurrPage());
        return page;
    }

    @Override
    public List<String> titleForRemote(String key) {
        return baseMapper.titleForRemote(key);
    }

    @Override
    public void addLikeSum(Long id) {
        baseMapper.addLikeSum(id);

        // 点赞后也进行索引更新
        // 替换为部分字段更新
        Long likeNum = this.selectById(id).getLikeNum();
        Map<String, Object> jsonMap = new HashMap<>(1);
        jsonMap.put("likeNum",likeNum);
        searchService.updatePartial(id.toString(),jsonMap);
    }

    @Override
    public void addViewSum(Long id) {
        baseMapper.addViewSum(id);
        //更新es中的浏览数
        Long viewNum=this.selectById(id).getViewNum();
        Map<String, Object> jsonMap = new HashMap<>(1);
        jsonMap.put("viewNum",viewNum);
        searchService.updatePartial(id.toString(),jsonMap);
    }
}
