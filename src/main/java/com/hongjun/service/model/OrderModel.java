package com.hongjun.service.model;

import com.hongjun.dataobject.ItemDO;
import com.hongjun.dataobject.UserDO;

import java.math.BigDecimal;

/**
 * @author hongjun500
 * @date 2020/6/26 14:44
 * Created with 2019.3.2.IntelliJ IDEA
 * Description:用户下单交易模型
 */
public class OrderModel {
    /**订单交易流水号*/
    private String id;

    /**下单用户*/
    private UserModel userModel;

    /**购买商品*/
    private ItemModel itemModel;

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

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public ItemModel getItemModel() {
        return itemModel;
    }

    public void setItemModel(ItemModel itemModel) {
        this.itemModel = itemModel;
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
