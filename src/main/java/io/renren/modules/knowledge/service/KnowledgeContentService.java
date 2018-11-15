package io.renren.modules.knowledge.service;

import com.baomidou.mybatisplus.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.knowledge.entity.KnowledgeContentEntity;

import java.util.List;
import java.util.Map;

/**
 * @author hunji
 * @date 2018/11/5
 */
public interface KnowledgeContentService  extends IService<KnowledgeContentEntity> {
    /**
     * 获取分页数据
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String,Object> params);

    /**
     * 保存内容
     *
     * @param knowledgeContent
     */
    void save(KnowledgeContentEntity knowledgeContent);

    /**
     * 更新内容
     *
     * @param knowledgeContent
     */
    void update(KnowledgeContentEntity knowledgeContent);

    /**
     * 批量删除类型
     *
     * @param ids
     */
    void deleteBatch(Long[] ids);

    /**
     * 分页获取公共知识库
     * state 为 1 的数据代表通过审核的数据
     * @param params
     * @return
     */
    PageUtils commonContentPage(Map<String,Object> params);

    /**
     * 审核通过
     *
     * @param id
     */
    void review(Long id);

    /**
     * 返回重填
     * @param id
     */
    void sendback(Long id);
    /**
     * 根据类型获取知识库内容的数量
     * @param typeIds
     * @return
     */
    int getCountByType(Long[] typeIds);


    /**
     * DTO分页查询
     * @param map
     * @return
     */
    PageUtils queryDTO(Map<String, Object> map);

    /**
     * 知识库内容使用title进行匹配方法
     * @param key 关键字
     * @return
     */
    List<String> titleForRemote(String key);

    /**
     * 知识库内容进行点赞
     * @param id id值
     * @return
     */
    void addLikeSum(Long id);

    /**
     * 知识库内容浏览后
     * @param id
     */
    void addViewSum(Long id);

}
