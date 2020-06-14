package com.hongjun.service.impl;

import com.hongjun.dao.UserDOMapper;
import com.hongjun.dao.UserPasswordDOMapper;
import com.hongjun.dataobject.UserDO;
import com.hongjun.dataobject.UserPasswordDO;
import com.hongjun.service.UserService;
import com.hongjun.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hongjun500
 * @date 2020/6/13 22:59
 * Created with 2019.3.2.IntelliJ IDEA
 * Description:
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    @Override
    public UserModel getUserById(Integer id) {
        // 调用对应的userDOMapper获取对应用户dataobject
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        // 此处不应该直接DO里面的数据返回给前端，须定义一个model
        if (userDO==null){
            return null;
        }
        // 通过用户id获取对应用户的加密密码
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        return convertFromUserModel(userDO, userPasswordDO);
    }

    private UserModel convertFromUserModel(UserDO userDO, UserPasswordDO userPasswordDO){
       if (userDO==null){
            return null;
       }
        UserModel userModel = new UserModel();
        // 将查询到DO数据copy到Model
        BeanUtils.copyProperties(userDO, userModel);
        // 此处不能再直接用copy,UserModel里面的主键和UserPasswordDO里面的重复了
        if (userPasswordDO!=null){
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }
        return userModel;
    }
}
