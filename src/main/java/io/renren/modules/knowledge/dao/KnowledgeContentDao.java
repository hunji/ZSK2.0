package io.renren.modules.knowledge.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import io.renren.modules.knowledge.dto.ContentDTO;
import io.renren.modules.knowledge.entity.KnowledgeContentEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author hunji
 * @date 2018/11/5
 */
@Mapper
public interface KnowledgeContentDao extends BaseMapper<KnowledgeContentEntity> {
    /**
     * DTO分页查找
     * @param map
     * @return
     */
    List<ContentDTO> queryDTO(Map<String, Object> map);

    /**
     * DTO查找数量
     * @param map
     * @return
     */
    int queryDTOCount(Map<String, Object> map);

    /**
     * 知识库内容使用title进行匹配方法
     * @param key 关键字
     * @return
     */
    List<String> titleForRemote(String key);

    /**
     * 点赞
     * @param id id值
     * @return
     */
    void addLikeSum(Long id);

    /**
     * 浏览数
     * @param id
     */
    void addViewSum(Long id);
}
