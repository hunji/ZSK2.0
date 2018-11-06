package io.renren.common.utils;

import io.renren.modules.sys.entity.SysUserEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Assert;
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
public class RedisUtilsTest {

    @Autowired
    private RedisUtils redisUtils;

    @Test
    public void set() {
        redisUtils.set("ceshi","123");

        Assert.assertEquals(redisUtils.get("ceshi") ,"123");
    }

    @Test
    public void contextLoads() {
        SysUserEntity user = new SysUserEntity();
        user.setEmail("qqq@qq.com");
        redisUtils.set("user", user);


        System.out.println(ToStringBuilder.reflectionToString(redisUtils.get("user", SysUserEntity.class)));
    }

    /*
    @Test
    public void delete() {
        //redisUtils.delete("ceshi");
    }
    * */
}