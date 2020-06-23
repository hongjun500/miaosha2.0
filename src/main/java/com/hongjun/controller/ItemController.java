package com.hongjun.controller;

import com.hongjun.controller.viewobject.ItemVO;
import com.hongjun.error.BusinessException;
import com.hongjun.response.CommonReturnType;
import com.hongjun.service.ItemService;
import com.hongjun.service.model.ItemModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @author hongjun500
 * @date 2020/6/23 22:41
 * Created with 2019.3.2.IntelliJ IDEA
 * Description:
 */
@Controller("item")
@RequestMapping(value = "/item")
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;

    public CommonReturnType createItem(@RequestParam(name = "title") String title,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "stock") Integer stock,
                                       @RequestParam(name = "description") String description,
                                       @RequestParam(name = "imgUrl") String imgUrl) throws BusinessException {

        // 封装service请求用来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setDescription(description);
        itemModel.setImgUrl(imgUrl);

        ItemModel resultItem = itemService.createItem(itemModel);
        ItemVO itemVO = convertItemVOFromItemModel(resultItem);
        return CommonReturnType.create(itemVO);
    }

    private ItemVO convertItemVOFromItemModel(ItemModel itemModel){
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        return itemVO;
    }
}
