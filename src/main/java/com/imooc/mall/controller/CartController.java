package com.imooc.mall.controller;

import com.imooc.mall.consts.MallConst;
import com.imooc.mall.form.CartAddForm;
import com.imooc.mall.form.CartUpdateForm;
import com.imooc.mall.pojo.User;
import com.imooc.mall.service.impl.CartServiceImpl;
import com.imooc.mall.vo.CartVo;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
public class CartController {

    @Autowired
    CartServiceImpl cartService;

    @PostMapping("/carts")
    public ResponseVo<CartVo> add(@Valid @RequestBody CartAddForm cartAddForm,
                                  HttpSession session) {
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return cartService.add(user.getId(), cartAddForm);
    }

    @GetMapping("/carts")
    public ResponseVo<CartVo> list(HttpSession session) {
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return cartService.list(user.getId());
    }

    @PutMapping("/carts/{productId}")
    public ResponseVo<CartVo> update(@Valid @RequestBody CartUpdateForm cartUpdateForm,
                                     @PathVariable("productId") Integer productId,
                                     HttpSession session) {
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return cartService.update(user.getId(), productId, cartUpdateForm);
    }

    @DeleteMapping("/carts/{productId}")
    public ResponseVo<CartVo> delete(@PathVariable("productId") Integer productId,
                                     HttpSession session) {
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return cartService.delete(user.getId(),productId);
    }

    @PutMapping("/carts/selectAll")
    public ResponseVo<CartVo> selectAll(HttpSession session){
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return cartService.selectAllOrNone(user.getId(),true);
    }

    @PutMapping("/carts/unSelectAll")
    public ResponseVo<CartVo> unSelectAll(HttpSession session){
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return cartService.selectAllOrNone(user.getId(),false);
    }

    @GetMapping("/carts/products/sum")
    public ResponseVo<Integer> sum(HttpSession session){
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return cartService.sum(user.getId());
    }
}
