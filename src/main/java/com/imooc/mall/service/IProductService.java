package com.imooc.mall.service;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.pojo.Product;
import com.imooc.mall.vo.ProductVo;

import java.util.List;

public interface IProductService {
    PageInfo<List<ProductVo>> list(Integer categoryId, Integer pageNum, Integer pageSize);

    Product productDetail(Integer productId);
}
