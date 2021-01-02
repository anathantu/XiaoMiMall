package com.imooc.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.consts.MallConst;
import com.imooc.mall.form.OrderCreateForm;
import com.imooc.mall.pojo.User;
import com.imooc.mall.service.impl.OrderServiceImpl;
import com.imooc.mall.vo.OrderVo;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
public class OrderController {

    @Autowired
    OrderServiceImpl orderService;

    @PostMapping("/orders")
    public ResponseVo create(@Valid @RequestBody OrderCreateForm orderCreateForm,
                             HttpSession session){
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return orderService.create(orderCreateForm,user.getId());
    }

    @GetMapping("/orders")
    public ResponseVo<PageInfo> list(@RequestParam(required = false,defaultValue = "10") Integer pageSize,
                                     @RequestParam(required = false,defaultValue = "1") Integer pageNum,
                                     HttpSession session){
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return orderService.list(pageNum,pageSize,user.getId());
    }

    @GetMapping("/orders/{orderNo}")
    public ResponseVo<OrderVo> detail(@PathVariable("orderNo") Long orderNo,
                                      HttpSession session){
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return orderService.detail(user.getId(),orderNo);
    }

    @PutMapping("/orders/{orderNo}")
    public ResponseVo cancel(@PathVariable Long orderNo,
                             HttpSession session){
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return orderService.cancel(user.getId(),orderNo);
    }
}
