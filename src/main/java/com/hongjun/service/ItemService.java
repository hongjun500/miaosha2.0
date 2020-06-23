package com.hongjun.service;

import com.hongjun.error.BusinessException;
import com.hongjun.service.model.ItemModel;

import java.util.List;

/**
 * @author hongjun500
 * @date 2020/6/23 22:00
 * Created with 2019.3.2.IntelliJ IDEA
 * Description:
 */
public interface ItemService {
    /**
     * 创建商品
     */
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    /**
     * 商品列表浏览
     */
    List<ItemModel> listItem();

    /**
     * 商品详情浏览
     */
    ItemModel getItemById(Integer id);
}
