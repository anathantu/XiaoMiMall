package com.imooc.mall.service.impl;

import com.imooc.mall.MallApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class CategoryServiceImplTest extends MallApplicationTests {

    @Autowired
    CategoryServiceImpl categoryService;

    @Test
    public void selectAll() {
        System.out.println(categoryService.selectAll().getData());
    }

    @Test
    public void findSubCategoryId() {
        List<Integer> subCategoryId = categoryService.findSubCategoryId(100001);
        for (Integer i:subCategoryId)
            System.out.println(i);
    }
}
