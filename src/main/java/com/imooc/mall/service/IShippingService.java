package com.imooc.mall.service;

import com.imooc.mall.form.ShippingForm;
import com.imooc.mall.vo.ResponseVo;

import java.util.Map;

public interface IShippingService {


    public ResponseVo<Map<String, Integer>> add(Integer id, ShippingForm form);

    public ResponseVo<Map<String, String>> delete(Integer id, Integer shippingId);

    public ResponseVo update(Integer id, Integer shippingId, ShippingForm form);

    public ResponseVo list(Integer pageNum, Integer pageSize, Integer id);
}
