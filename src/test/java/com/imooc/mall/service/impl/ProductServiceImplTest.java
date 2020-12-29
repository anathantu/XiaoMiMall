package com.imooc.mall.service.impl;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.MallApplicationTests;
import com.imooc.mall.vo.ProductVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ProductServiceImplTest extends MallApplicationTests {

    @Autowired
    ProductServiceImpl productService;

    @Test
    public void list() {
        PageInfo<List<ProductVo>> list = productService.list(100001,1,10);
        System.out.println("1");
    }

    @Test
    public void detail(){

    }
}
