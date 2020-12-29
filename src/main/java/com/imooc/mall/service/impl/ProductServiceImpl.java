package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.dao.ProductMapper;
import com.imooc.mall.pojo.Product;
import com.imooc.mall.service.IProductService;
import com.imooc.mall.vo.ProductVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryServiceImpl categoryService;

    @Override
    public PageInfo<List<ProductVo>> list(Integer categoryId,Integer pageNum,Integer pageSize) {
        if(pageNum<=0)
            pageNum=1;
        if(pageSize<=0)
            pageSize=10;

        List<Integer> subCategoryId = categoryService.findSubCategoryId(categoryId);
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList=productMapper.selectByCategoryList(subCategoryId);
        List<ProductVo> list = new ArrayList<>();
        for(Product product:productList){
            ProductVo productVo = new ProductVo();
            BeanUtils.copyProperties(product,productVo);
            list.add(productVo);
        }
        PageInfo page = new PageInfo(productList);
        page.setList(list);
        return page;
    }

    @Override
    public Product productDetail(Integer productId) {
        return productMapper.selectByPrimaryKey(productId);
    }
}
