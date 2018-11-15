package io.renren.modules.knowledge.search;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Longs;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.modules.knowledge.dto.ContentDTO;
import io.renren.modules.knowledge.dto.ESContentDTO;
import io.renren.modules.knowledge.entity.KnowledgeContentEntity;
import io.renren.modules.knowledge.service.KnowledgeContentService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
    private RestHighLevelClient esClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String INDEX_NAME = "zsk";
    private static final String INDEX_TYPE = "knoweledge";

    @Override
    public void index(Long contentId) {

        // 查出来实体，转成indextemplate    TODO:typeName的处理
        KnowledgeContentEntity entity = contentService.selectById(contentId);
        if (entity == null) {
            log.error("找不到id为{}的KnowledgeContentEntity", contentId);
            return;
        }

        KnoweledgeIndexTemplate template = new KnoweledgeIndexTemplate();
        template.setId(contentId);
        template.setBrief(entity.getBrief());
        template.setContent(entity.getContent());
        template.setLikeNum(entity.getLikeNum());
        template.setReviewDate(entity.getReviewDate());
        template.setTitle(entity.getTitle());
        template.setViewNum(entity.getViewNum());
        template.setTypeId(entity.getTypeId());
        template.setUserName(entity.getUserName());

        // 对应的在es中有三种情况  create  update delete&create
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.termQuery(KnoweledgeIndexKey.KNOWLEDGE_ID, contentId));
        SearchRequest request=new SearchRequest(INDEX_NAME).types(INDEX_TYPE).source(searchSourceBuilder);

        boolean success;
        try {
            SearchHits hits =this.esClient.search(request).getHits();
            long totalHits =hits.getTotalHits();
            if (totalHits == 0) {
                success = create(template);
            } else if (totalHits == 1) {
                String esId = hits.getAt(0).getId();
                success = update(esId, template);
            } else {
                success = deleteAndCreate(totalHits, template);
            }

            if (success) {
                log.info("成功索引" + template.getId());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void remove(Long contentId) {
        //先要查出来es中的id再进行删除
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.termQuery(KnoweledgeIndexKey.KNOWLEDGE_ID, contentId));
        SearchRequest request=new SearchRequest(INDEX_NAME).types(INDEX_TYPE).source(searchSourceBuilder);
        try {
            SearchHits hits = this.esClient.search(request).getHits();
            //如果没有命中返回不进行删除操作
            if(hits.totalHits==0){
                return;
            }
            DeleteRequest deleteRequest = new DeleteRequest(INDEX_NAME, INDEX_TYPE, hits.getAt(0).getId());
            this.esClient.delete(deleteRequest);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * es中添加索引
     *
     * @param indexTemplate
     * @return
     */
    private boolean create(KnoweledgeIndexTemplate indexTemplate) {
        try {
            IndexRequest request = new IndexRequest(INDEX_NAME, INDEX_TYPE);
            request.source(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON);
            IndexResponse response = esClient.index(request);

            log.debug("Create index with house: " + indexTemplate.getId());
            if (response.status() == RestStatus.CREATED) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            log.error("Error to index knoweledge " + indexTemplate.getId(), e);
            return false;
        }
    }

    /**
     * es中更新索引
     *
     * @param esId
     * @param indexTemplate
     * @return
     */
    private boolean update(String esId, KnoweledgeIndexTemplate indexTemplate) {
        try {
            UpdateRequest request=new UpdateRequest(INDEX_NAME, INDEX_TYPE, esId);
            request.doc(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON);
            UpdateResponse response = this.esClient.update(request);

            log.debug("Update index with knoweledge: " + indexTemplate.getId());
            if (response.status() == RestStatus.OK) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            log.error("Error to index knoweledge " + indexTemplate.getId(), e);
            return false;
        }
    }

    /**
     * es中先删除再添加索引
     *
     * @param totalHit
     * @param indexTemplate
     * @return
     */
    private boolean deleteAndCreate(long totalHit, KnoweledgeIndexTemplate indexTemplate) {

        //  由于restclient不能根据查询条件去删除（transclient可以），这里分两步：先查出来符合条件的数据id；然后批量删除

        // 0.1根据条件查找
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.termQuery(KnoweledgeIndexKey.KNOWLEDGE_ID, indexTemplate.getId()));

        SearchRequest request=new SearchRequest(INDEX_NAME).types(INDEX_TYPE).source(searchSourceBuilder);
        try {
            SearchHits hits =this.esClient.search(request).getHits();
            List<String> docIds = new ArrayList<>(hits.getHits().length);
            for (SearchHit hit : hits) {
                docIds.add(hit.getId());
            }

            // 0.2批量删除
            BulkRequest bulkRequest = new BulkRequest();
            for (String id : docIds) {
                DeleteRequest deleteRequest = new DeleteRequest(INDEX_NAME, INDEX_TYPE, id);
                bulkRequest.add(deleteRequest);
            }
            this.esClient.bulk(bulkRequest);
        } catch (IOException e) {
            log.error("先删除后添加操作失败", totalHit);
            return false;
        }

        return create(indexTemplate);
    }

    /**
     * es索引查找
     *
     * @return
     */
    @Override
    public ESContentDTO queryIndex(Map<String, Object> params) {
        //region 获取前台传来的参数
        String key = (String) params.get("key");
        String viewCount = (String) params.get("viewCount");
        String dianzanCount = (String) params.get("dianzanCount");
        String dateRange = (String) params.get("dateRange");
        int page = Integer.parseInt(params.get("page").toString()) ;
        int limit = Integer.parseInt(params.get("limit").toString()) ;
        Query query = new Query(params);
        //endregion
        Long count=0L;
        ESContentDTO returnData=new ESContentDTO();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //region 处理查询条件

        // 当有点赞数的参数传来时进行筛选
        if(dianzanCount !=null && !dianzanCount.isEmpty() && Long.parseLong(dianzanCount)>0){
            Long dianzanCountNum = Long.parseLong(dianzanCount) ;
            RangeQueryBuilder likeNumQueryBuilder = QueryBuilders.rangeQuery("likeNum").gte(dianzanCountNum);
            boolQueryBuilder.must(likeNumQueryBuilder);
        }

        // 当有浏览数的参数传来时进行筛选
        if(viewCount !=null && !viewCount.isEmpty() && Long.parseLong(viewCount)>0){
            Long viewCountNum = Long.parseLong(viewCount) ;
            RangeQueryBuilder viewCountQueryBuilder = QueryBuilders.rangeQuery("viewNum").gte(viewCountNum);
            boolQueryBuilder.must(viewCountQueryBuilder);
        }

        // 当有时间的参数限制传来时进行筛选
        Date nowDate = new Date();
        RangeQueryBuilder reviewDateQueryBuilder = QueryBuilders.rangeQuery("reviewDate");
        if(dateRange!=null && !dateRange.isEmpty() && dateRange!="全部"){
            Date startDate;
            switch (dateRange){
                case "一周内":
                    startDate = DateUtils.addDateWeeks(nowDate, -1);
                    reviewDateQueryBuilder.gte(DateUtils.formatTime(startDate));
                    break;
                case"一月内":
                    startDate = DateUtils.addDateMonths(nowDate, -1);
                    reviewDateQueryBuilder.gte(DateUtils.formatTime(startDate));
                    break;
                case"三月内":
                    startDate = DateUtils.addDateMonths(nowDate, -3);
                    reviewDateQueryBuilder.gte(DateUtils.formatTime(startDate));
                    break;
                case"一年内":
                    startDate = DateUtils.addDateYears(nowDate, -1);
                    reviewDateQueryBuilder.gte(DateUtils.formatTime(startDate));
                    break;
                default:
                    log.error("日期区间参数输入错误");
            }
            boolQueryBuilder.must(reviewDateQueryBuilder);
        }

        // 传来关键字进行全文检索
        if(key!=null && !key.isEmpty()){
            MultiMatchQueryBuilder multiMatchQueryBuilder=QueryBuilders
                    .multiMatchQuery(key,KnoweledgeIndexKey.TITLE,KnoweledgeIndexKey.BRIEF,KnoweledgeIndexKey.CONTENT);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }

        //endregion
        searchSourceBuilder.query(boolQueryBuilder);
        // 分页
        searchSourceBuilder.from((page-1)*limit).size(limit);
        List<ContentDTO> knowledgeInfos= new ArrayList<>();
        SearchRequest request=new SearchRequest(INDEX_NAME).types(INDEX_TYPE).source(searchSourceBuilder);
        try {
            SearchResponse response = esClient.search(request);
            SearchHits hits =response.getHits();
            count=hits.totalHits;
            // 获取es的查询消耗时间和查询出的总数量
            returnData.setTook(response.getTook().getMillis());
            returnData.setTotalHits(hits.getTotalHits());
            // region 读取es中的内容到dto中
            for (SearchHit hit : hits) {
                String es_title = String.valueOf(hit.getSourceAsMap().get(KnoweledgeIndexKey.TITLE));
                String es_brief = String.valueOf(hit.getSourceAsMap().get(KnoweledgeIndexKey.BRIEF));
                String es_content = String.valueOf(hit.getSourceAsMap().get(KnoweledgeIndexKey.CONTENT));
                String es_userName = String.valueOf(hit.getSourceAsMap().get(KnoweledgeIndexKey.USER_NAME));
                Long es_id = Longs.tryParse(String.valueOf(hit.getSourceAsMap().get(KnoweledgeIndexKey.KNOWLEDGE_ID)));
                Long es_like =Longs.tryParse(String.valueOf(hit.getSourceAsMap().get(KnoweledgeIndexKey.LIKE_NUM)));
                Long es_view =Longs.tryParse(String.valueOf(hit.getSourceAsMap().get(KnoweledgeIndexKey.VIEW_NUM)));
                Long es_typeID =Longs.tryParse(String.valueOf(hit.getSourceAsMap().get(KnoweledgeIndexKey.TYPE_ID)));
                String esDateStr=String.valueOf(hit.getSourceAsMap().get(KnoweledgeIndexKey.REVIEW_DATE));
                Date es_date = new Date();
                if(esDateStr!=null && !esDateStr.isEmpty() &&esDateStr!="null"){
                    es_date=DateUtils.stringToDate(esDateStr,"yyyy-MM-dd HH:mm:ss");
                }

                ContentDTO dto=new ContentDTO();
                dto.setTitle(es_title);
                dto.setBrief(es_brief);
                dto.setContent(es_content);
                dto.setUserName(es_userName);
                dto.setId(es_id);
                dto.setLikeNum(es_like);
                dto.setViewNum(es_view);
                dto.setTypeId(es_typeID);
                dto.setReviewDate(es_date);
                knowledgeInfos.add(dto);
            }
            //endregion

        } catch (IOException e) {
            e.printStackTrace();
        }
        returnData.setPage(new PageUtils(knowledgeInfos, count.intValue(), query.getLimit(), query.getCurrPage()));
        return returnData;
    }

    @Override
    public boolean updatePartial(String contentId, Map<String, Object> jsonMap) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.termQuery(KnoweledgeIndexKey.KNOWLEDGE_ID, contentId));
        SearchRequest request=new SearchRequest(INDEX_NAME).types(INDEX_TYPE).source(searchSourceBuilder);
        SearchHits hits = null;
        try {
            hits = this.esClient.search(request).getHits();
            String esId = hits.getAt(0).getId();
            UpdateRequest updateRequest = new UpdateRequest(INDEX_NAME, INDEX_TYPE, esId)
                    .doc(jsonMap);
            UpdateResponse response = this.esClient.update(updateRequest);
            if (response.status() == RestStatus.OK) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            log.error("es部分更新字段错误，传入contentId为：" + contentId+e.getMessage());
            return false;
        }
    }
}