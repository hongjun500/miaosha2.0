package com.hongjun.service;

import com.hongjun.error.BusinessException;
import com.hongjun.service.model.UserModel;

/**
 * @author hongjun500
 * @date 2020/6/13 22:58
 * Created with 2019.3.2.IntelliJ IDEA
 * Description:
 */
public interface UserService {
    /**
     * 通过id获取用户对象
     * @param id
     */
    UserModel getUserById(Integer id);

    /**
     * 用户注册
     * @param userModel
     * @return
     */
    void register(UserModel userModel) throws BusinessException;
}
