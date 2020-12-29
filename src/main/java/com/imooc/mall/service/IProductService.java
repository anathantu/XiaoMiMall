package com.imooc.mall.service;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.vo.ProductDetailVo;
import com.imooc.mall.vo.ProductVo;

import java.util.List;

public interface IProductService {
    PageInfo<List<ProductVo>> list(Integer categoryId, Integer pageNum, Integer pageSize);

    ProductDetailVo productDetail(Integer productId);
}
