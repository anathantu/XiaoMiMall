package com.imooc.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.service.impl.ProductServiceImpl;
import com.imooc.mall.vo.ProductDetailVo;
import com.imooc.mall.vo.ProductVo;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseVo<PageInfo<List<ProductVo>>> list(@RequestParam(required = false,defaultValue = "0") Integer categoryId,
                                                          @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                          @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        PageInfo<List<ProductVo>> pageInfo = productService.list(categoryId, pageNum, pageSize);
        return ResponseVo.success(pageInfo);
    }

    @GetMapping("/products/{productId}")
    public ResponseVo<ProductDetailVo> detail(@PathVariable("productId") Integer productId){
        ProductDetailVo product = productService.productDetail(productId);
        if(product==null)
            return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
        return ResponseVo.success(product);
    }
}
