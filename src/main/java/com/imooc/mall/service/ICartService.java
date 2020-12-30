package com.imooc.mall.service;

import com.imooc.mall.form.CartAddForm;
import com.imooc.mall.form.CartUpdateForm;
import com.imooc.mall.vo.CartVo;
import com.imooc.mall.vo.ResponseVo;

public interface ICartService {

    public ResponseVo<CartVo> add(Integer uid, CartAddForm cartAddForm);

    public ResponseVo<CartVo> list(Integer uid);

    public ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm cartUpdateForm);

    public ResponseVo<CartVo> delete(Integer uid, Integer productId);

    public ResponseVo<CartVo> selectAllOrNone(Integer id , Boolean selectAll);

    public ResponseVo<Integer> sum(Integer id);
}
