package com.hongjun.controller;

import com.hongjun.controller.viewobject.UserVO;
import com.hongjun.error.BusinessException;
import com.hongjun.error.EmBusinessError;
import com.hongjun.response.CommonReturnType;
import com.hongjun.service.UserService;
import com.hongjun.service.model.UserModel;
import org.apache.ibatis.annotations.Mapper;
import org.omg.CORBA.UNKNOWN;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hongjun500
 * @date 2020/6/13 22:54
 * Created with 2019.3.2.IntelliJ IDEA
 * Description:
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/get/{id}")
    @ResponseBody
    public CommonReturnType getUser(@PathVariable(value = "id") Integer id) throws BusinessException {
        // 调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

        // 若对象不存在
        if (userModel == null){
            // userModel.setEncrptPassword("fdfdsfsd");
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        // 将核心领域模型的对象返回给前端UI
        UserVO userVO = convertFromModel(userModel);
        // 返回通用对象
        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel){
        if (userModel == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }
}
