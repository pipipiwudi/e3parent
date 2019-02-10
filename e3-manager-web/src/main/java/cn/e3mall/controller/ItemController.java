package cn.e3mall.controller;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @RequestMapping("/item/{itemId}")
    @ResponseBody
    public TbItem getItemById(@PathVariable Long itemId){
        TbItem item = itemService.getItemById(itemId);
        return item;
    }

    @RequestMapping("/ite/{itemId}")

    public String getNameById(@PathVariable Long itemId){

        String name = itemService.getNameById(itemId);
        return name;
    }

    @RequestMapping("/item/list")
    @ResponseBody
    public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
        EasyUIDataGridResult result = itemService.getItemList(page, rows);
        return result;
    }


    @RequestMapping(value = "/item/save",method = RequestMethod.POST)
    @ResponseBody
    public E3Result addItem(TbItem item,String desc){
        E3Result result = itemService.addItem(item, desc);
        return result;
    }

    @RequestMapping(value = "/rest/item/query/item/desc/{itemId}")
    @ResponseBody
    public E3Result getItemDesc(@PathVariable Long itemId){
        E3Result desc = itemService.getItemDesc(itemId);
        return desc;
    }

//    @RequestMapping(value = "/rest/item/param/item/query/{itemId}")
//    @ResponseBody
//    public E3Result getItemParam(@PathVariable Long itemId){
//        E3Result param = itemService.getItemParam(itemId);
//        return param;
//    }

    @RequestMapping("/rest/item/update")
    @ResponseBody
    public E3Result updateItem(TbItem item,String desc){
        E3Result result = itemService.updateItem(item, desc);
        return result;
    }

    @RequestMapping(value = "/rest/item/delete")
    @ResponseBody
    public E3Result deleteItem(@RequestParam("ids") String ids){
        String[] itemIds = ids.split(",");
        E3Result result = itemService.deleteItem(itemIds);
        return result;
    }
}
