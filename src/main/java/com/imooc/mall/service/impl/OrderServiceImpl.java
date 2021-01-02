package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.dao.OrderItemMapper;
import com.imooc.mall.dao.OrderMapper;
import com.imooc.mall.dao.ProductMapper;
import com.imooc.mall.dao.ShippingMapper;
import com.imooc.mall.enums.OrderStatusEnum;
import com.imooc.mall.enums.ProductStatusEnum;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.form.OrderCreateForm;
import com.imooc.mall.pojo.*;
import com.imooc.mall.service.IOrderService;
import com.imooc.mall.util.OrderUtils;
import com.imooc.mall.vo.OrderVo;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements IOrderService {


    @Autowired
    ShippingMapper shippingMapper;

    @Autowired
    CartServiceImpl cartService;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public ResponseVo create(OrderCreateForm orderCreateForm, Integer uid) {
        //收货地址校验
        Shipping shipping = shippingMapper.selectByPrimaryKey(orderCreateForm.getShippingId());
        if (shipping == null) {
            return ResponseVo.error(ResponseEnum.SHIPPING_NOT_EXIST);
        }

        //获取购物车数据并校验
        List<Cart> cartList = cartService.selectedCartList(uid);
        if (cartList.isEmpty()) {
            return ResponseVo.error(ResponseEnum.CART_SELECTED_IS_EMPTY);
        }

        //获取cartList里的productIds
        Set<Integer> productIdSet = cartList.stream()
                .map(Cart::getProductId)
                .collect(Collectors.toSet());
        List<Product> productList = productMapper.selectByProductIdSet(productIdSet);
        Map<Integer, Product> map  = productList.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        //创建订单编号
        Long orderNo = generateOrderNo();

        //创建订单
        //先添加商品
        List<OrderItem> orderItems = new ArrayList<>();
        for (Cart cart : cartList) {
            Product product = map.get(cart.getProductId());
            //校验产品
            if (product == null) {
                return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST);
            }

            if (!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())) {
                return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
            }

            if (product.getStock() < cart.getQuantity()) {
                return ResponseVo.error(ResponseEnum.PROODUCT_STOCK_ERROR);
            }

            OrderItem orderItem = OrderUtils.buildOrderItem(uid, orderNo, cart.getQuantity(), product);
            orderItems.add(orderItem);

            //减库存
            product.setStock(product.getStock() - orderItem.getQuantity());
            int row = productMapper.updateByPrimaryKeySelective(product);
            if (row <= 0) {
                return ResponseVo.error(ResponseEnum.ERROR);
            }
        }

        //生成订单
        Order order = OrderUtils.buildOrder(uid, orderNo, orderCreateForm.getShippingId(), orderItems);

        int row = orderMapper.insertSelective(order);
        //TODO 这里测试一下回滚是否能进行，感觉应该要抛出异常才行
        if (row == 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }

        int rows = orderItemMapper.batchInsert(orderItems);
        if (row <= 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }

        //更新购物车信息
        for (Cart cart : cartList) {
            cartService.delete(uid, cart.getProductId());
        }

        OrderVo orderVo = OrderUtils.buildOrderVo(order, orderItems, shipping);
        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo<PageInfo> list(Integer pageNum, Integer pageSize, Integer uid) {
        PageHelper.startPage(pageNum, pageSize);
        List<OrderVo> list = new ArrayList<>();

        List<Order> orderList = orderMapper.selectByUid(uid);

        Set<Long> orderNoSet = orderList.stream()
                .map(Order::getOrderNo)
                .collect(Collectors.toSet());
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoSet(orderNoSet);
        Map<Long, List<OrderItem>> orderItemMap = orderItemList.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderNo));

        Set<Integer> shippingIdSet = orderList.stream()
                .map(Order::getShippingId)
                .collect(Collectors.toSet());
        List<Shipping> shippingList = shippingMapper.selectByOrderNoSet(shippingIdSet);
        Map<Integer, Shipping> shippingMap = shippingList.stream()
                .collect(Collectors.toMap(Shipping::getId, shipping -> shipping));

        //对每个order构造一个orderVo
        for (Order order : orderList) {
            OrderVo orderVo = OrderUtils.buildOrderVo(order,
                    orderItemMap.get(order.getOrderNo()),
                    shippingMap.get(order.getShippingId()));
            list.add(orderVo);
        }
        PageInfo pageInfo = new PageInfo(list);
        pageInfo.setList(list);

        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<OrderVo> detail(Integer uid, Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(uid)) {
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }

        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);

        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());

        OrderVo orderVo = OrderUtils.buildOrderVo(order, orderItemList, shipping);
        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo cancel(Integer uid, Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(uid)) {
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }
        //只有[未付款]订单可以取消，看自己公司业务
        if (!order.getStatus().equals(OrderStatusEnum.NO_PAY.getCode())) {
            return ResponseVo.error(ResponseEnum.ORDER_STATUS_ERROR);
        }

        order.setStatus(OrderStatusEnum.CANCELED.getCode());
        order.setCloseTime(new Date());
        int row = orderMapper.updateByPrimaryKeySelective(order);
        if (row <= 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }

        return ResponseVo.success();
    }


    /**
     * 企业级：分布式唯一id/主键
     *
     * @return
     */
    private Long generateOrderNo() {
        return System.currentTimeMillis() + new Random().nextInt(999);
    }
}
