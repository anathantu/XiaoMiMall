package com.imooc.mall.dao;

import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.pojo.Category;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CategoryMapperTest extends MallApplicationTests {

    @Autowired
    CategoryMapper categoryMapper;

    @Test
    public void findById() {
        Category byId = categoryMapper.findById(100001);
        System.out.println(byId);
    }

    @Test
    public void queryById() {
        Category category = categoryMapper.queryById(100001);
        System.out.println(category);
    }
}
