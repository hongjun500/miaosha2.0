package com.hongjun.controller;

import com.hongjun.controller.viewobject.ItemVO;
import com.hongjun.controller.viewobject.OrderVO;
import com.hongjun.controller.viewobject.UserVO;
import com.hongjun.error.BusinessException;
import com.hongjun.error.EmBusinessError;
import com.hongjun.response.CommonReturnType;
import com.hongjun.service.OrderService;
import com.hongjun.service.model.ItemModel;
import com.hongjun.service.model.OrderModel;
import com.hongjun.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hongjun500
 * @date 2020/6/26 18:22
 * Created with 2019.3.2.IntelliJ IDEA
 * Description:
 */
@Controller
@RequestMapping(value = "/order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @ResponseBody
    @RequestMapping(value = "/createOrder", method = RequestMethod.POST)
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                                        @RequestParam(name = "amount") Integer amount) throws BusinessException {

        // 获取用户登录信息 小布尔可能会出现空指针异常
        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if (isLogin == null || !isLogin){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登录,不能下单");
        }
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
        OrderModel orderModel = orderService.createOrder(userModel.getId(), itemId, amount);
        return CommonReturnType.create(null);
    }

    @RequestMapping(value = "/getOrderById/{id}")
    @ResponseBody
    public CommonReturnType getOrderById(@PathVariable(name = "id") String id) throws BusinessException {
        OrderModel orderModel = orderService.getOrderDetail(id);
        OrderVO orderVO = convertOrderVOFromOrderModel(orderModel);
        return CommonReturnType.create(orderVO);
    }

    @RequestMapping(value = "/getOrderByIdToView/{id}")
    public String getOrderByIdToView(@PathVariable(name = "id") String id, Model model) throws BusinessException {
        OrderModel orderModel = orderService.getOrderDetail(id);
        OrderVO orderVO = convertOrderVOFromOrderModel(orderModel);
        model.addAttribute("orderVO", orderVO);
        return "getOrder";
    }

    private OrderVO convertOrderVOFromOrderModel(OrderModel orderModel){
        if (orderModel == null){
            return null;
        }
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orderModel, orderVO);
        UserVO userVO = convertUserVOFromUserModel(orderModel.getUserModel());
        ItemVO itemVO = convertItemVOFromItemModel(orderModel.getItemModel());
        orderVO.setUserVO(userVO);
        orderVO.setItemVO(itemVO);
        return orderVO;
    }
    private UserVO convertUserVOFromUserModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }
    private ItemVO convertItemVOFromItemModel(ItemModel itemModel){
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        return itemVO;
    }
}
