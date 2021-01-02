package com.imooc.mall.service.impl;

import com.google.gson.Gson;
import com.imooc.mall.dao.ProductMapper;
import com.imooc.mall.enums.ProductStatusEnum;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.form.CartAddForm;
import com.imooc.mall.form.CartUpdateForm;
import com.imooc.mall.pojo.Cart;
import com.imooc.mall.pojo.Product;
import com.imooc.mall.service.ICartService;
import com.imooc.mall.vo.CartProductVo;
import com.imooc.mall.vo.CartVo;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements ICartService {

    private final static String CART_REDIS_KEY_TEMPLATE = "cart_%d";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    Gson gson;

    @Override
    public ResponseVo<CartVo> add(Integer uid, CartAddForm cartAddForm) {
        Integer quantity = 1;

        Product product = productMapper.selectByPrimaryKey(cartAddForm.getProductId());

        //对商品数据进行校验
        if (product == null) {
            return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST);
        }

        if (!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())) {
            return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
        }

        if (product.getStock() <= 0) {
            return ResponseVo.error(ResponseEnum.PROODUCT_STOCK_ERROR);
        }

        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        Cart cart;
        String value = opsForHash.get(redisKey, String.valueOf(product.getId()));
        if (StringUtils.isEmpty(value)) {
            cart = new Cart(product.getId(), quantity, cartAddForm.isSelected());
        } else {
            cart = gson.fromJson(value, Cart.class);
            cart.setQuantity(cart.getQuantity() + 1);
        }

        opsForHash.put(redisKey, String.valueOf(cartAddForm.getProductId()), gson.toJson(cart));

        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> list(Integer uid) {
        CartVo cartVo = new CartVo();
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(redisKey);

        boolean selectAll = true;
        Integer cartTotalQuantity = 0;
        BigDecimal cartTotalPrice = BigDecimal.ZERO;

        List<CartProductVo> cartProductVoList = new ArrayList<>();
        //每一个key都是productId
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            Integer productId = Integer.valueOf(entry.getKey());
            Cart cart = gson.fromJson(entry.getValue(), Cart.class);

            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if (product != null) {
                CartProductVo cartProductVo = new CartProductVo(productId,
                        cart.getQuantity(),
                        product.getName(),
                        product.getSubtitle(),
                        product.getMainImage(),
                        product.getPrice(),
                        product.getStatus(),
                        product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
                        product.getStock(),
                        cart.getProductSelected()
                );
                cartProductVoList.add(cartProductVo);

                if (!cart.getProductSelected()) {
                    selectAll = false;
                }

                if (cart.getProductSelected()) {
                    cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice());
                }
            }
            cartTotalQuantity += cart.getQuantity();
        }

        cartVo.setSelectedAll(selectAll);
        cartVo.setCartTotalQuantity(cartTotalQuantity);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        return ResponseVo.success(cartVo);
    }

    @Override
    public ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm cartUpdateForm) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();

        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        String value = opsForHash.get(redisKey, String.valueOf(productId));

        if(value==null){
            //购物车里没有此商品
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);
        }

        Cart cart = gson.fromJson(value,Cart.class);
        if(cartUpdateForm.getQuantity()!=null&&cartUpdateForm.getQuantity()>=0){
            cart.setQuantity(cartUpdateForm.getQuantity());
        }

        if(cartUpdateForm.getSelected()!=null){
            cart.setProductSelected(cartUpdateForm.getSelected());
        }

        opsForHash.put(redisKey,String.valueOf(productId),gson.toJson(cart));
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> delete(Integer uid, Integer productId) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();

        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE,uid);
        String value = opsForHash.get(redisKey, String.valueOf(productId));
        if (StringUtils.isEmpty(value)) {
            //没有该商品, 报错
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);
        }
        opsForHash.delete(redisKey,String.valueOf(productId));

        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> selectAllOrNone(Integer uid,Boolean selectAll) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE,uid);
        Map<String, String> entries = opsForHash.entries(redisKey);
        for(Map.Entry<String,String> entry:entries.entrySet()){
            Cart cart = gson.fromJson(entry.getValue(),Cart.class);
            cart.setProductSelected(selectAll);
            opsForHash.put(redisKey, entry.getKey(), gson.toJson(cart));
        }
        return list(uid);
    }

    @Override
    public ResponseVo<Integer> sum(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE,uid);
        Map<String, String> entries = opsForHash.entries(redisKey);
        Integer sum=0;
        for(Map.Entry<String,String> entry:entries.entrySet()){
            Cart cart = gson.fromJson(entry.getValue(),Cart.class);
            sum+=cart.getQuantity();
        }
        return ResponseVo.success(sum);
    }

    public List<Cart> allCartList(Integer uid){
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE,uid);
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        Map<String, String> entries = opsForHash.entries(redisKey);
        List<Cart> list=new ArrayList<>();
        for(Map.Entry<String,String> entry:entries.entrySet()){
            Cart cart = gson.fromJson(entry.getValue(),Cart.class);
            list.add(cart);
        }
        return list;
    }

    public List<Cart> selectedCartList(Integer uid){
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE,uid);
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        Map<String, String> entries = opsForHash.entries(redisKey);
        List<Cart> list=new ArrayList<>();
        for(Map.Entry<String,String> entry:entries.entrySet()){
            Cart cart = gson.fromJson(entry.getValue(),Cart.class);
            if(cart.getProductSelected())
                list.add(cart);
        }
        return list;
    }
}
