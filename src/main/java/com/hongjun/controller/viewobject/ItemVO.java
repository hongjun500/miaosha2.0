package com.hongjun.controller.viewobject;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author hongjun500
 * @date 2020/6/23 22:42
 * Created with 2019.3.2.IntelliJ IDEA
 * Description:
 */
public class ItemVO {
    private Integer id;

    /**商品名*/
    private String title;

    /**价格*/
    private BigDecimal price;

    /**商品库存*/
    private Integer stock;

    /**商品描述信息*/
    private String description;

    /**商品销量*/
    private Integer sales;

    /**商品图片*/
    private String imgUrl;
}
