package com.imooc.mall.controller;

import com.imooc.mall.service.impl.ProductServiceImpl;
import com.imooc.mall.vo.ProductVo;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    ProductServiceImpl productService;

    //TODO 查找品类可以用缓存保存，提高性能
    //暂时先采用查找品类及子品类，再找到品类下所有商品的方法
    @GetMapping("/products")
    public ResponseVo<List<ProductVo>> products(@RequestParam("categoryId") Integer categoryId){
        List<ProductVo> productVoList=productService.list(categoryId);
        return ResponseVo.success(productVoList);
    }
}
