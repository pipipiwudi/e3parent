package cn.e3mall.controller;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ContentCategoryController {

    @Autowired
    private ContentCategoryService contentCategoryService;

    @RequestMapping("content/category/list")
    @ResponseBody
    public List<EasyUITreeNode> getContentList(@RequestParam(name = "id",defaultValue = "0")Long parentId){
        List<EasyUITreeNode> categoryList = contentCategoryService.getCategoryList(parentId);
        return categoryList;
    }

    @RequestMapping(value = "/content/category/create",method = RequestMethod.POST)
    @ResponseBody
    public E3Result createCategory(Long parentId,String name){
        E3Result result = contentCategoryService.addContentCategory(parentId, name);
        return result;
    }

    @RequestMapping(value = "content/category/update",method = RequestMethod.POST)
    @ResponseBody
    public E3Result updateCategory(Long id,String name){
        E3Result result = contentCategoryService.updateContentCategory(id, name);
        return result;
    }

    @RequestMapping(value = "content/category/delete",method = RequestMethod.POST)
    @ResponseBody
    public E3Result deleteCategory(Long id){
        E3Result result = contentCategoryService.deleteContentCategory(id);
        return result;
    }
}
