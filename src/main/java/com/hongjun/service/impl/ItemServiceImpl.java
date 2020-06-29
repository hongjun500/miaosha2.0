package com.hongjun.service.impl;

import com.hongjun.dao.ItemDOMapper;
import com.hongjun.dao.ItemStockDOMapper;
import com.hongjun.dataobject.ItemDO;
import com.hongjun.dataobject.ItemStockDO;
import com.hongjun.error.BusinessException;
import com.hongjun.error.EmBusinessError;
import com.hongjun.service.ItemService;
import com.hongjun.service.model.ItemModel;
import com.hongjun.validator.ValidationResult;
import com.hongjun.validator.ValidatorImpl;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.awt.AWTAccessor;

import java.math.BigDecimal;
import java.nio.BufferOverflowException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hongjun500
 * @date 2020/6/23 22:05
 * Created with 2019.3.2.IntelliJ IDEA
 * Description:
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;


    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        // 校验入参
        ValidationResult result = validator.validate(itemModel);
        if (result.getHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrorMsg());
        }
        // 转发itemModel -> dataobject
        ItemDO itemDO = convertItemDOFromItemModel(itemModel);

        // 写入数据库
        itemDOMapper.insertSelective(itemDO);
        // 拿到入库操作成功之后的itemDO
        itemModel.setId(itemDO.getId());

        ItemStockDO itemStockDO = convertItemStockDOFromItemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);
        // 返回创建完成的对象
        return getItemById(itemModel.getId());
    }

    /**
     * 把itemModel转换成itemDO
     * @param itemModel
     * @return
     */
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

    private ItemStockDO convertItemStockDOFromItemModel(ItemModel itemModel){
        if (itemModel==null){
            return null;
        }
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());
        return itemStockDO;
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> itemDOList = itemDOMapper.listItem();
        List<ItemModel> itemModelList = itemDOList.stream().map(itemDO -> {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            ItemModel itemModel = convertModelFromDataObject(itemDO, itemStockDO);
            return itemModel;
        }).collect(Collectors.toList());
        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if (itemDO==null){
            return null;
        }
        // 获得库存数量
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());

        // 将dataobject -> model
        ItemModel itemModel = convertModelFromDataObject(itemDO, itemStockDO);
        return itemModel;
    }

    private ItemModel convertModelFromDataObject(ItemDO itemDO, ItemStockDO itemStockDO){
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO, itemModel);
        itemModel.setPrice(BigDecimal.valueOf(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());

        return itemModel;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) {
        // 影响的条目数
        int affectedRow = itemStockDOMapper.decreaseStock(itemId, amount);
        if (affectedRow > 0){
            // 更新库存成功
            return true;
        }else {
            // 更新库存失败
            return false;
        }
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) {
        itemDOMapper.increaseSales(itemId, amount);
    }
}
