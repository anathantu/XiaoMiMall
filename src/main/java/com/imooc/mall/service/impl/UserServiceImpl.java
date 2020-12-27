package com.imooc.mall.service.impl;

import com.imooc.mall.dao.UserMapper;
import com.imooc.mall.enums.ResponseEnum;
import com.imooc.mall.enums.RoleEnum;
import com.imooc.mall.pojo.User;
import com.imooc.mall.service.IUserService;
import com.imooc.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ResponseVo<User> register(User user) {
        //先对数据校验
        int countByUsername = userMapper.countByUsername(user.getUsername());
        if(countByUsername>0){
            return ResponseVo.error(ResponseEnum.USERNAME_EXIST);
        }

        int countByEmail = userMapper.countByEmail(user.getEmail());
        if(countByEmail > 0){
            return ResponseVo.error(ResponseEnum.EMAIL_EXIST);
        }

        user.setRole(RoleEnum.CUSTOMER.getCode());
        //MD5摘要算法
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));
        //写入数据库
        int resultCount = userMapper.insertSelective(user);
        if(resultCount==0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo<User> login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if(user==null){
            //用户不存在 返回用户名或者密码错误
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }

        if(!user.getPassword().equalsIgnoreCase(
                DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)))){
            //密码错误，返回用户名或者密码错误
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }

        log.info(user.toString());

        user.setPassword("");
        return ResponseVo.success(user);
    }
}
