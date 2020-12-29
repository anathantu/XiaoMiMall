package com.imooc.mall.service.impl;

import com.imooc.mall.MallApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CategoryServiceImplTest extends MallApplicationTests {

    @Autowired
    CategoryServiceImpl categoryService;

    @Test
    public void selectAll() {
        System.out.println(categoryService.selectAll().getData());
    }

    @Test
    public void findSubCategoryId() {
        List<Integer> subCategoryId = categoryService.findSubCategoryId(null);
        for (Integer i:subCategoryId)
            System.out.println(i);
    }
}
