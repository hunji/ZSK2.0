package io.renren.modules.knowledge.service.impl;

import io.renren.modules.knowledge.service.KnowledgeTypeService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author hunji
 * @date 2018/11/5
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class KnowledgeTypeServiceImplTest {
    @Autowired
    private KnowledgeTypeService knowledgeTypeService;

    @Test
    public void queryPage() throws Exception {
        Map<String, Object> map = new HashMap<>();
        Integer num= knowledgeTypeService.queryPage(map).getList().size();
        Assert.assertNotEquals(num,new Integer(0));
    }

    @Test
    public void save() throws Exception {
    }

    @Test
    public void update() throws Exception {
    }

    @Test
    public void deleteBatch() throws Exception {
        Long[] typeIds={new Long(1)};

        knowledgeTypeService.deleteBatch(typeIds);
    }

}