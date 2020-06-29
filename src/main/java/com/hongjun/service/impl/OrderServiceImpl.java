package com.hongjun.service.impl;

import com.hongjun.dao.OrderDOMapper;
import com.hongjun.dao.SequenceDOMapper;
import com.hongjun.dataobject.ItemDO;
import com.hongjun.dataobject.OrderDO;
import com.hongjun.dataobject.SequenceDO;
import com.hongjun.dataobject.UserDO;
import com.hongjun.error.BusinessException;
import com.hongjun.error.EmBusinessError;
import com.hongjun.service.ItemService;
import com.hongjun.service.OrderService;
import com.hongjun.service.UserService;
import com.hongjun.service.model.ItemModel;
import com.hongjun.service.model.OrderModel;
import com.hongjun.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author hongjun500
 * @date 2020/6/26 16:54
 * Created with 2019.3.2.IntelliJ IDEA
 * Description:
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDOMapper orderDOMapper;

    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer amount) throws BusinessException {
        // 1.校验下单状态：下单商品是否存在，下单用户是否合法，下单数量是否正确
        ItemModel itemModel = itemService.getItemById(itemId);
        if (itemModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商品信息不存在!");
        }

        UserModel userModel = userService.getUserById(userId);
        if (userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        if (amount<0 || amount > 99){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "数量不正确!");
        }

        // 2.落单减库存/支付减库存(处理较为繁琐，容易出现超卖现象)，根据实际情况处理
        // 此处使用落单减库存
        boolean result = itemService.decreaseStock(itemId, amount);
        if (!result){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        // 3，订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserModel(userModel);
        orderModel.setItemModel(itemModel);
        orderModel.setAmount(amount);
        orderModel.setItemPrice(itemModel.getPrice());
        orderModel.setOrderPrice(itemModel.getPrice().multiply(new BigDecimal(amount)));

        // 生成交易流水订单号
        orderModel.setId(generatorOrderNo());
        OrderDO orderDO = this.convertOrderDOFromOrderModel(orderModel);
        orderDOMapper.insertSelective(orderDO);
        // 4.返回前端
        itemService.increaseSales(itemId, amount);
        return orderModel;
    }

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();
        String format = now.format(DateTimeFormatter.ISO_DATE);
        String time = format.replace("-", "");
        System.out.println(time);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private String generatorOrderNo(){
        // 16位订单号
        StringBuilder stringBuilder = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        String format = now.format(DateTimeFormatter.ISO_DATE);
        String nowDate = format.replace("-", "");
        // 前8位为时间信息，年月日
        stringBuilder.append(nowDate);
        // 中间6位为自增序列
        // 当业务场景下如果超过了6位时，应该设置一个最大值，(sequenceDO.getCurrentValue() + sequenceDO.getStep())达到最大值时,就把CurrentValue初始化为0，循环如此
        int sequence = 0;                                        //在数据库中初始化了
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        // 当前的值加上步长
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
        // 更新
        sequenceDOMapper.updateByPrimaryKey(sequenceDO);


        String sequenceStr = String.valueOf(sequence);
        for (int i = 0 ;i< 6-sequenceStr.length(); i++){
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);

        // 最后2位为分库分表位,此处不做讨论，直接简单用00
        stringBuilder.append("00");
        return stringBuilder.toString();
    }

    private UserDO convertUserDOFromUserModel(UserModel userModel){
        if (userModel == null){
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel, userDO);
        return userDO;
    }

    private ItemDO convertItemDOFromItemModel(ItemModel itemModel){
        if (itemModel==null){
            return null;
        }
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel, itemDO);
        // 价格的转换有精度问题，需要手动设置
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        return itemDO;
    }

    private OrderDO convertOrderDOFromOrderModel(OrderModel orderModel){
        if (orderModel == null){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel, orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        // 由于OrderModel里面直接使用类定义属性
        // 手动操作
        orderDO.setItemId(orderModel.getItemModel().getId());
        orderDO.setUserId(orderModel.getUserModel().getId());
        return orderDO;
    }

    @Override
    public OrderModel getOrderDetail(String id) throws BusinessException {
        OrderDO orderDO = orderDOMapper.selectByPrimaryKey(id);
        if (orderDO == null){
            throw new BusinessException(EmBusinessError.ITEM_NOT_EXIST);
        }
        OrderModel orderModel = convertOrderDOFromOrderDO(orderDO);
        return orderModel;
    }

    private OrderModel convertOrderDOFromOrderDO(OrderDO orderDO){
        if (orderDO == null){
            return null;
        }
        OrderModel orderModel = new OrderModel();
        BeanUtils.copyProperties(orderDO, orderModel);
        Integer userId = orderDO.getUserId();
        Integer itemId = orderDO.getItemId();
        if (itemId == null || userId == null){
            return null;
        }
        UserModel userModel = userService.getUserById(orderDO.getUserId());
        ItemModel itemModel = itemService.getItemById(orderDO.getItemId());
        orderModel.setUserModel(userModel);
        orderModel.setItemModel(itemModel);
        orderModel.setOrderPrice(BigDecimal.valueOf(orderDO.getOrderPrice()));
        orderModel.setItemPrice(BigDecimal.valueOf(orderDO.getItemPrice()));
        return orderModel;
    }
}
