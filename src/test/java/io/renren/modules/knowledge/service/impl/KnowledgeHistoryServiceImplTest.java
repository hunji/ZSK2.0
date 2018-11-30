package io.renren.modules.knowledge.service.impl;

import io.renren.common.utils.PageUtils;
import io.renren.modules.knowledge.entity.KnowledgeHistoryEntity;
import io.renren.modules.knowledge.service.KnowledgeHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author hunji
 * @date 2018/11/29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@Transactional
public class KnowledgeHistoryServiceImplTest {

    @Autowired
    private KnowledgeHistoryService knowledgeHistoryService;

    @Test
    public void addEntity() {
        KnowledgeHistoryEntity newEntity=new KnowledgeHistoryEntity();
        newEntity.setFlowId("2");
        newEntity.setFlowName("2");
        newEntity.setFlowsn("2");
        newEntity.setUseContent("测试内容");
        knowledgeHistoryService.save(newEntity);

        KnowledgeHistoryEntity knowledgeHistoryEntity = knowledgeHistoryService.selectById(2L);
        log.info(knowledgeHistoryEntity.toString());
    }

    @Test
    public void queryPage() {
        Map<String, Object> params= new HashMap<>();
        params.put("userId",new Long(1));
        PageUtils page= knowledgeHistoryService.queryPage(params);
        log.info(page.getList().toString());
    }
}