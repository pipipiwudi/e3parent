package cn.e3mall.controller;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.pojo.TbContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class ContentController {
    @Autowired
    private ContentService contentService;

    @RequestMapping(value = "content/query/list")
    @ResponseBody
    public EasyUIDataGridResult getContentList(Long categoryId,Integer page, Integer rows){
        EasyUIDataGridResult list = contentService.getContentList(categoryId, page, rows);
        return list;
    }

    @RequestMapping(value = "content/save",method = RequestMethod.POST)
    @ResponseBody
    public E3Result addContent(TbContent tbContent){
        tbContent.setCreated(new Date());
        tbContent.setUpdated(new Date());
        E3Result result = contentService.addContent(tbContent);
        return result;
    }

    @RequestMapping(value = "rest/content/edit",method = RequestMethod.POST)
    @ResponseBody
    public E3Result updateContent(TbContent tbContent){
        tbContent.setUpdated(new Date());
        E3Result result = contentService.updateContent(tbContent);
        return result;
    }
}
