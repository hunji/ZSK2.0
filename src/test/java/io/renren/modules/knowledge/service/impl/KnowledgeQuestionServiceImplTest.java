package io.renren.modules.knowledge.service.impl;

import io.renren.common.utils.PageUtils;
import io.renren.modules.knowledge.entity.KnowledgeQuestionEntity;
import io.renren.modules.knowledge.service.KnowledgeQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author hunji
 * @date 2018/12/4
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@Transactional
public class KnowledgeQuestionServiceImplTest {

    @Autowired
    private KnowledgeQuestionService questionService;
    @Test
    public void queryPage() {
        Map<String, Object> params= new HashMap<>();
        PageUtils page= questionService.queryPage(params);
        log.info(page.getList().toString());
    }

    @Test
    public void chatContent(){
        Map<String, Object> params= new HashMap<>();
        params.put("userName","admin");
        params.put("SID","hunji");
        List<KnowledgeQuestionEntity> knowledgeQuestionEntities = questionService.chatContent(params);
        log.info(knowledgeQuestionEntities.toString());
    }
}