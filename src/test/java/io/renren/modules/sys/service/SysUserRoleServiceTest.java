package io.renren.modules.sys.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author hunji
 * @date 2018/12/4
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@Transactional
public class SysUserRoleServiceTest {
    @Autowired
    private SysUserRoleService userRoleService;
    @Test
    public void queryRoleByUserName() {
        List<String> list = userRoleService.queryRoleByUserName("guest");
        log.info(list.toString());
    }
}