package com.imooc.mall.service;

import com.imooc.mall.vo.ProductVo;

import java.util.List;

public interface IProductService {
    List<ProductVo> list(Integer categoryId);
}
