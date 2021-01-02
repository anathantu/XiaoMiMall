package com.imooc.mall.service;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.form.OrderCreateForm;
import com.imooc.mall.vo.OrderVo;
import com.imooc.mall.vo.ResponseVo;

public interface IOrderService {
    ResponseVo create(OrderCreateForm orderCreateForm, Integer uid);

    ResponseVo<PageInfo> list(Integer pageNum, Integer pageSize, Integer id);

    ResponseVo<OrderVo> detail(Integer id, Long orderNo);

    ResponseVo cancel(Integer uid, Long orderNo);
}
