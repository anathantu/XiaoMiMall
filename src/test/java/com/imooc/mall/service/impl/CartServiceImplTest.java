package com.imooc.mall.service.impl;

import com.imooc.mall.MallApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

public class CartServiceImplTest extends MallApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void add() {
        stringRedisTemplate.opsForValue().set("yunai","shuai");
    }
}
