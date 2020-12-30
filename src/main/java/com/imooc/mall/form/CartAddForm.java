package com.imooc.mall.form;

import javax.validation.constraints.NotNull;

public class CartAddForm {

    @NotNull
    private Integer productId;

    private boolean selected;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
