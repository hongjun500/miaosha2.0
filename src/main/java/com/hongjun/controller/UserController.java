package com.hongjun.controller;

import com.hongjun.controller.viewobject.UserVO;
import com.hongjun.response.CommonReturnType;
import com.hongjun.service.UserService;
import com.hongjun.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.websocket.server.PathParam;

/**
 * @author hongjun500
 * @date 2020/6/13 22:54
 * Created with 2019.3.2.IntelliJ IDEA
 * Description:
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/get/{id}")
    @ResponseBody
    public CommonReturnType getUser(@PathVariable(value = "id") Integer id){
        // 调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);
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
