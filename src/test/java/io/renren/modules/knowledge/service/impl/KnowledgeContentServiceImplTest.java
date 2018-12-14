package io.renren.modules.knowledge.service.impl;

import io.renren.common.utils.PageUtils;
import io.renren.modules.knowledge.entity.KnowledgeContentEntity;
import io.renren.modules.knowledge.service.KnowledgeContentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
 * @date 2018/11/5
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@Transactional
public class KnowledgeContentServiceImplTest {

    // TODO:这里应该在测试前先初始化测试数据，不应该受到数据库当前数据的影响

    @Autowired
    private KnowledgeContentService contentService;

    @Test
    public void queryPage() {
        Map<String, Object> params= new HashMap<>();
        params.put("userId",new Long(1));
        PageUtils page= contentService.queryPage(params);
        log.info(page.getList().toString());

        params.put("userId",new Long(3));
        page= contentService.queryPage(params);
        log.info(page.getList().toString());
    }

    @Test
    public void commonContentPage() {
        Map<String, Object> params= new HashMap<>();
        PageUtils page= contentService.commonContentPage(params);
        log.info(page.getList().toString());
    }

    @Test
    public void save() {
        KnowledgeContentEntity entity=new KnowledgeContentEntity();
        entity.setTitle("save测试");
        contentService.save(entity);
        Assert.assertEquals(contentService.selectCount(null),4);
    }

    @Test
    public void update() {
        KnowledgeContentEntity entity=contentService.selectById(1);
        entity.setTitle("id1的测试件进行修改");
        contentService.update(entity);
        assertEquals(entity.getTitle(),"id1的测试件进行修改");
    }

    @Test
    public void deleteBatch() {
        Long[] params={new Long(1)};
        contentService.deleteBatch(params);
        int count=contentService.selectCount(null);
        assertEquals(count,2);
    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void review() {
        KnowledgeContentEntity entity=contentService.selectById(1);
        assertEquals(entity.getRstate(),new Integer(0));

        contentService.review(entity.getId());


        assertEquals(contentService.selectById(1).getRstate(),new Integer(1));
    }

    @Test
    public void queryDTO() {
        Map<String, Object> params= new HashMap<>();
        PageUtils page= contentService.queryDTO(params);
        log.info(page.toString());
        log.info(page.getList().toString());
    }

    @Test
    public void titleForRemote() {
        String key="用";
        log.info(contentService.titleForRemote(key).toString());
    }

    @Test
    public void addLikeSum() {
        Long numBefore=contentService.selectById(new Long(1)).getLikeNum();
        log.info(numBefore.toString());
        contentService.addLikeSum(new Long(1));

        Long numAfter=contentService.selectById(new Long(1)).getLikeNum();
        log.info(numAfter.toString());
        Assert.assertEquals(++numBefore,numAfter);
    }
}