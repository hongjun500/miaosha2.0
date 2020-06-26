package com.hongjun.controller.viewobject;

import com.hongjun.service.model.ItemModel;
import com.hongjun.service.model.UserModel;

import java.math.BigDecimal;

/**
 * @author hongjun500
 * @date 2020/6/26 20:42
 * Created with 2019.3.2.IntelliJ IDEA
 * Description:
 */
public class OrderVO {
    /**订单交易流水号*/
    private String id;

    /**下单用户*/
    private UserVO userVO;

    /**购买商品*/
    private ItemVO itemVO;

    /**购买的商品单价
     * (冗余字段/商品价格会发生变法)
     * */
    private BigDecimal itemPrice;

    /**购买数量*/
    private Integer amount;

    /**购买金额*/
    private BigDecimal orderPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserVO getUserVO() {
        return userVO;
    }

    public void setUserVO(UserVO userVO) {
        this.userVO = userVO;
    }

    public ItemVO getItemVO() {
        return itemVO;
    }

    public void setItemVO(ItemVO itemVO) {
        this.itemVO = itemVO;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }
}
