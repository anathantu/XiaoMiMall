package com.imooc.mall.form;

import javax.validation.constraints.NotBlank;

public class UserRegisterForm {


    //@NotEmpt 用于集合
//    @NotNull
    @NotBlank(message = "用户名不能为空") //用于 String 判断空格
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
