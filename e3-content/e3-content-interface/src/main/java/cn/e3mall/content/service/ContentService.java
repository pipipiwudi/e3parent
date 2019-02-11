package cn.e3mall.content.service;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbContent;

import java.util.List;


public interface ContentService {
    EasyUIDataGridResult getContentList(Long categoryId, int page, int row);
    E3Result addContent(TbContent tbContent);
    E3Result updateContent(TbContent tbContent);
    List<TbContent> getContentListByCid(Long cid);
}
