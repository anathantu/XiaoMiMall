package com.imooc.mall.service;

import com.imooc.mall.vo.CategoryVo;
import com.imooc.mall.vo.ResponseVo;

import java.util.List;

public interface ICategoryService {

    /**
     * 查询商品品类
     */
    public ResponseVo<List<CategoryVo>> selectAll();
}
