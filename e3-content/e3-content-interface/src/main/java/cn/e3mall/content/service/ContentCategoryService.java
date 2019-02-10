package cn.e3mall.content.service;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;

import java.util.List;

public interface ContentCategoryService {
    List<EasyUITreeNode> getCategoryList(Long parentId);
    E3Result addContentCategory(Long parentId,String name);
    E3Result updateContentCategory(Long id,String name);
    E3Result deleteContentCategory(Long id);
}
