package com.imooc.mall.enums;

public enum RoleEnum {
    ADMIN(0),
    CUSTOMER(1);

    Integer code;

    RoleEnum(Integer code){
        this.code=code;
    }

    public Integer getCode() {
        return code;
    }
}
