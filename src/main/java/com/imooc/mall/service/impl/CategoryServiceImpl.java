package com.imooc.mall.service.impl;

import com.imooc.mall.consts.MallConst;
import com.imooc.mall.dao.CategoryMapper;
import com.imooc.mall.pojo.Category;
import com.imooc.mall.service.ICategoryService;
import com.imooc.mall.vo.CategoryVo;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public static Map<Integer, List<CategoryVo>> map = null;

    @Override
    public ResponseVo<List<CategoryVo>> selectAll() {

        if (map != null) {
            return ResponseVo.success(map.getOrDefault(MallConst.ROOT_PARENT_ID, new ArrayList<>()));
        }
        map = new HashMap<>();
        findSubCategories();
        return ResponseVo.success(map.getOrDefault(MallConst.ROOT_PARENT_ID, new ArrayList<>()));
    }

    public List<Integer> findSubCategoryId(Integer categoryId) {
        List<Integer> list = new ArrayList<>();
        list.add(categoryId == null ? MallConst.ROOT_PARENT_ID : categoryId);
        if (map == null) {
            map = new HashMap<>();
            findSubCategories();
        }
        findSubCategoryIdList(categoryId == null ? MallConst.ROOT_PARENT_ID : categoryId, list);

        return list;
    }

    private void findSubCategories() {
        List<Category> categories = categoryMapper.selectAll();
        List<CategoryVo> list = new ArrayList<>();
        for (Category category : categories) {
            CategoryVo categoryVo = new CategoryVo();
            BeanUtils.copyProperties(category, categoryVo);
            list.add(categoryVo);
            List<CategoryVo> subList = map.getOrDefault(category.getParentId(), null);
            if (subList == null)
                subList = new ArrayList<>();
            subList.add(categoryVo);
            map.put(category.getParentId(), subList);
        }

        for (CategoryVo categoryVo : list) {
            categoryVo.setSubCategories(map.getOrDefault(categoryVo.getId(), new ArrayList<>()));
        }
    }

    private void findSubCategoryIdList(Integer categoryId, List<Integer> list) {
        List<CategoryVo> categoryVoList = map.getOrDefault(categoryId, new ArrayList<>());
        if (categoryVoList == null || categoryVoList.size() == 0)
            return;
        for (CategoryVo categoryVo : categoryVoList) {
            list.add(categoryVo.getId());
            findSubCategoryIdList(categoryVo.getId(), list);
        }
    }
}
