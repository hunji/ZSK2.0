package io.renren.modules.knowledge.search;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import io.renren.modules.knowledge.entity.KnowledgeContentEntity;
import io.renren.modules.knowledge.service.KnowledgeContentService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.IOException;
import java.util.List;

/**
 * @author hunji
 * @date 2018/11/5
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SearchServiceImplTest {

    @Autowired
    private ISearchService searchService;
    @Autowired
    private KnowledgeContentService KnowledgeContentService;

    @Autowired
    private RestHighLevelClient esClient;

    private static final String INDEX_NAME = "zsk";
    private static final String INDEX_TYPE = "knoweledge";
    @Test
    public void index() {
        List<KnowledgeContentEntity> list = KnowledgeContentService.selectList(new EntityWrapper<KnowledgeContentEntity>() {
        });
        for (KnowledgeContentEntity knowledgeContentEntity : list) {
            searchService.index(knowledgeContentEntity.getId());
        }
    }

    @Test
    public void remove() {
        List<KnowledgeContentEntity> list = KnowledgeContentService.selectList(new EntityWrapper<KnowledgeContentEntity>() {
        });
        for (KnowledgeContentEntity knowledgeContentEntity : list) {
            searchService.remove(knowledgeContentEntity.getId());
        }
    }

    @Test
    public void mutiSearch() throws IOException {
        Long count=0L;
        //1.0创建searchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //2.0拼接查询
        //不同条件按照
        RangeQueryBuilder likeNumQueryBuilder = QueryBuilders.rangeQuery("likeNum").gte(6);
        MultiMatchQueryBuilder queryBuilder=QueryBuilders
                    .multiMatchQuery("误差",KnoweledgeIndexKey.TITLE,KnoweledgeIndexKey.BRIEF,KnoweledgeIndexKey.CONTENT);
        //TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("tag", "体育");

        //使用boolbuilder把这几个查询用must连接起来
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(likeNumQueryBuilder).must(queryBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        // 分页
        searchSourceBuilder.from(0).size(10);
        SearchRequest request=new SearchRequest(INDEX_NAME).types(INDEX_TYPE).source(searchSourceBuilder);
        long totalHits = esClient.search(request).getHits().totalHits;
        Assert.assertEquals(totalHits,1);
    }

}