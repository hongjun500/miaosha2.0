package com.hongjun.service.impl;

import com.hongjun.dao.UserDOMapper;
import com.hongjun.dao.UserPasswordDOMapper;
import com.hongjun.dataobject.UserDO;
import com.hongjun.dataobject.UserPasswordDO;
import com.hongjun.error.BusinessException;
import com.hongjun.error.EmBusinessError;
import com.hongjun.service.UserService;
import com.hongjun.service.model.UserModel;
import com.hongjun.validator.ValidationResult;
import com.hongjun.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private ValidatorImpl validator;

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
        return convertUserModelFromUserDO(userDO, userPasswordDO);
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        if (userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        // 字段判空
        // if (StringUtils.isEmpty(userModel.getName()) || userModel.getAge() == null || userModel.getGender() == null || StringUtils.isEmpty(userModel.getTelphone())){
        //     // 内部处理空串
        //     /*public static boolean isEmpty(CharSequence cs) {
        //         return cs == null || cs.length() == 0;
        //     }
        //
        //     public static boolean isNotEmpty(CharSequence cs) {
        //         return !isEmpty(cs);
        //     }*/
        //     throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        // }
        ValidationResult result = validator.validate(userModel);
        if (result.getHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrorMsg());
        }

        // 实现model ---> dataobject的方法
        UserDO userDO = new UserDO();
        userDO = convertUserDOFromUserModel(userModel);
        // insertSelective对应的mybatis-mapper.xml里面sql语句的处理方式是：判断对应的字段在dataobject里面是否为null,如果不为null则执行insert操作，反之则跳过不执行insert操作--->依赖数据库，使用数据库中的默认值
        try {
            userDOMapper.insertSelective(userDO);
        }catch (DuplicateKeyException e){
            // 手机号重复
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号已注册!");
        }
        userModel.setId(userDO.getId());

        // 如果使用这种方式的话，当某个字段值为null的时候，执行insert时就会用null覆盖对应的默认值！！！
        // userDOMapper.insert(null);

        // 笔记--->  null不受唯一索引约束

        UserPasswordDO userPasswordDO = convertUserPasswordDOFromUserModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);
        // 此处的两个操作是在同一个事务下，须在方法上加上Transactional
        /*userDOMapper.insertSelective(userDO);
        userPasswordDOMapper.insertSelective(userPasswordDO);*/
    }

    @Override
    public UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException {
        // 通过用户手机号获取用户信息、
        UserDO userDO = userDOMapper.selectByPhone(telphone);
        if (userDO == null){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel = convertUserModelFromUserDO(userDO, userPasswordDO);

        // 比对用户输入的密码是否和用户加密的密码相匹配
        if (!encrptPassword.equals(userModel.getEncrptPassword())){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        return userModel;
    }

    private UserDO convertUserDOFromUserModel(UserModel userModel){
        if (userModel == null){
            return null;
        }
        UserDO userDO = new UserDO();
        // 把传递进来的userModel转变成userDO,对数据库写操作
        BeanUtils.copyProperties(userModel, userDO);
        return userDO;
    }

    private UserPasswordDO convertUserPasswordDOFromUserModel(UserModel userModel){
        if (userModel == null){
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        // 把传递进来的userModel转变成userDO,对数据库写操作
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }


    private UserModel convertUserModelFromUserDO(UserDO userDO, UserPasswordDO userPasswordDO){
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
