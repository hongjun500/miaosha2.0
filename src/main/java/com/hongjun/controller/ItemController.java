package com.hongjun.controller;

import com.hongjun.controller.viewobject.ItemVO;
import com.hongjun.error.BusinessException;
import com.hongjun.response.CommonReturnType;
import com.hongjun.service.ItemService;
import com.hongjun.service.model.ItemModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hongjun500
 * @date 2020/6/23 22:41
 * Created with 2019.3.2.IntelliJ IDEA
 * Description: 商品控制器
 */
@Controller("item")
@RequestMapping(value = "/item")
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;

    /**
     * 创建商品页面
     * @return
     */
    @GetMapping(value = "/createItemView")
    public String createItemView(){
        return "createItem";
    }
    @PostMapping(value = "/createItem")
    @ResponseBody
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

    /**
     * 查看商品详情
     * @param id
     * @return
     */
    @GetMapping(value = "/getItem1/{id}")
    @ResponseBody
    public CommonReturnType getItem(@PathVariable(name = "id") Integer id){
        ItemModel itemModel = itemService.getItemById(id);
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        return CommonReturnType.create(itemVO);
    }

    @GetMapping(value = "/getItem2/{id}")
    public String getItem2(@PathVariable(name = "id") Integer id, Model model){
        ItemModel itemModel = itemService.getItemById(id);
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        model.addAttribute("itemVO", itemVO);
        return "getItem";
    }

    /**
     * 商品列表数据获取
     * @return
     */
    @GetMapping(value = "/listItem")
    @ResponseBody
    public CommonReturnType listItem(){
        List<ItemModel> itemModelList = itemService.listItem();
        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
            ItemVO itemVO = convertItemVOFromItemModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOList);
    }

    /**
     * 商品列表页面
     * @return
     */
    @GetMapping(value = "/listItemView1")
    public String listItemView1(){
        return "listitem1";
    }

    /**
     * 商品列表页面(用thymeleaf获取)
     * @return
     */
    @GetMapping(value = "/listItemView2")
    public String listItemView2(Model model){
        List<ItemModel> itemModelList = itemService.listItem();
        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
            ItemVO itemVO = convertItemVOFromItemModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        // 取值到页面使用thymeleaf的语法获取数据
        model.addAttribute("itemVOList", itemVOList);
        return "listitem2";
    }

}
