package io.renren.modules.knowledge.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Longs;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.modules.knowledge.entity.KnowledgeContentEntity;
import io.renren.modules.knowledge.service.KnowledgeContentService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hunji
 * @date 2018/11/5
 */
@Slf4j
@Service
public class SearchServiceImpl implements ISearchService {

    @Autowired
    private KnowledgeContentService contentService;

    @Autowired
    private TransportClient esClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String INDEX_NAME = "zsk";
    private static final String INDEX_TYPE = "knoweledge";

    @Override
    public void index(Long contentId) {
        // 查出来实体，转成indextemplate    TODO:typeName的处理
        KnowledgeContentEntity entity = contentService.selectById(contentId);
        if(entity==null){
            log.error("找不到id为{}的KnowledgeContentEntity",contentId);
            return;
        }

        KnoweledgeIndexTemplate template=new KnoweledgeIndexTemplate();
        template.setId(contentId);
        template.setBrief(entity.getBrief());
        template.setContent(entity.getContent());
        template.setLikeNum(entity.getLikeNum());
        template.setReviewDate(entity.getReviewDate());
        template.setTitle(entity.getTitle());
        // 对应的在es中有三种情况  create  update delete&create
        SearchRequestBuilder requestBuilder= esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
                .setQuery(QueryBuilders.termQuery(KnoweledgeIndexKey.KNOWLEDGE_ID, contentId));
        log.info(requestBuilder.toString());

        boolean success;
        SearchResponse response=requestBuilder.get();
        long totalHits=response.getHits().getTotalHits();
        if(totalHits==0){
            success=create(template);
        }else if(totalHits==1){
            String esId=response.getHits().getAt(0).getId();
            success=update(esId,template);
        }else{
            success=deleteAndCreate(totalHits,template);
        }

        if(success){
            log.debug("成功索引" + template.getId());
        }
    }

    @Override
    public void remove(Long contentId) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(KnoweledgeIndexKey.KNOWLEDGE_ID, contentId))
                .source(INDEX_NAME);

        log.debug("Delete by query for knoweledge: " + builder.toString());

        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
    }

    /**
     * es中添加索引
     * @param indexTemplate
     * @return
     */
    private boolean create(KnoweledgeIndexTemplate indexTemplate) {
        try {
            IndexResponse response = esClient.prepareIndex(INDEX_NAME, INDEX_TYPE)
                    .setSource(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();
            log.debug("Create index with house: " + indexTemplate.getId());
            if (response.status() == RestStatus.CREATED) {
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            log.error("Error to index knoweledge " + indexTemplate.getId(), e);
            return false;
        }
    }

    /**
     * es中更新索引
     * @param esId
     * @param indexTemplate
     * @return
     */
    private boolean update(String esId, KnoweledgeIndexTemplate indexTemplate) {
        try {
            UpdateResponse response = this.esClient.prepareUpdate(INDEX_NAME, INDEX_TYPE, esId).setDoc(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();

            log.debug("Update index with knoweledge: " + indexTemplate.getId());
            if (response.status() == RestStatus.OK) {
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            log.error("Error to index knoweledge " + indexTemplate.getId(), e);
            return false;
        }
    }

    /**
     * es中先删除再添加索引
     * @param totalHit
     * @param indexTemplate
     * @return
     */
    private boolean deleteAndCreate(long totalHit, KnoweledgeIndexTemplate indexTemplate) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(KnoweledgeIndexKey.KNOWLEDGE_ID, indexTemplate.getId()))
                .source(INDEX_NAME);

        log.debug("Delete by query for knoweledge: " + builder);

        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        if (deleted != totalHit) {
            log.warn("Need delete {}, but {} was deleted!", totalHit, deleted);
            return false;
        } else {
            return create(indexTemplate);
        }
    }

    /**
     * es索引查找
     * @return
     */
    @Override
    public PageUtils queryIndex(Map<String, Object> params){
        String key= (String)params.get("key");
        Query query = new Query(params);

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(
                QueryBuilders.multiMatchQuery(key,
                        KnoweledgeIndexKey.TITLE,
                        KnoweledgeIndexKey.BRIEF,
                        KnoweledgeIndexKey.CONTENT)
        );
        SearchRequestBuilder requestBuilder=esClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQuery)
                //.setFrom(query.getCurrPage())
                //.setSize(query.getLimit())
                .setFetchSource(KnoweledgeIndexKey.KNOWLEDGE_ID,null);
        List<Long> knowledgeIds=new ArrayList<>();
        SearchResponse response = requestBuilder.get();
        if (response.status() != RestStatus.OK) {
            log.error("查询结果失败 " + requestBuilder);
            return new PageUtils(knowledgeIds, 0, 0, 0);
        }

        for (SearchHit hit : response.getHits()) {
            System.out.println(hit.getSourceAsString());
            knowledgeIds.add(Longs.tryParse(String.valueOf(hit.getSourceAsMap().get(KnoweledgeIndexKey.KNOWLEDGE_ID))));
        }
        return new PageUtils(knowledgeIds, (int)response.getHits().totalHits, query.getLimit(), query.getCurrPage());
    }
}