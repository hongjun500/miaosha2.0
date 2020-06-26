package com.hongjun.service;

import com.hongjun.error.BusinessException;
import com.hongjun.service.model.OrderModel;

/**
 * @author hongjun500
 * @date 2020/6/26 16:52
 * Created with 2019.3.2.IntelliJ IDEA
 * Description:
 */
public interface OrderService {
    /**
     * 创建订单
     * @param userId
     * @param itemId
     * @param amount
     * @return
     * @throws BusinessException
     */
    OrderModel createOrder(Integer userId, Integer itemId, Integer amount) throws BusinessException;

    /**查看订单详情*/
    OrderModel getOrderDetail(String id) throws BusinessException;
}
