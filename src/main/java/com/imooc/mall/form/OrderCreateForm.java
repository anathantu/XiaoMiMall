package com.imooc.mall.form;

import javax.validation.constraints.NotNull;

public class OrderCreateForm {

    @NotNull
    Integer shippingId;

    public Integer getShippingId() {
        return shippingId;
    }

    public void setShippingId(Integer shippingId) {
        this.shippingId = shippingId;
    }
}
