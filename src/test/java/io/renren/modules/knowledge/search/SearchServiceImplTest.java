package io.renren.modules.knowledge.search;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

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
    @Test
    public void index() {
        searchService.index(new Long(36));
        searchService.index(new Long(37));
        searchService.index(new Long(38));
    }

    @Test
    public void remove() {
        searchService.remove(new Long(39));
        searchService.remove(new Long(41));
        searchService.remove(new Long(40));
    }
}