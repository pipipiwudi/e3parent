package cn.e3mall.controller;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PageController {



    @RequestMapping("/")
    public String showIndex(){
        return "index";
    }

    @RequestMapping("{page}")
    public String showPage(@PathVariable String page){
        return page;
    }

    @RequestMapping("rest/page/item-edit")
    public  String editPage(){
        return "item-edit";
    }


//    @RequestMapping("rest/item/query/item/desc/{itemId}")
//    public String showDesc(@PathVariable Long itemId){
//
//    }

}
