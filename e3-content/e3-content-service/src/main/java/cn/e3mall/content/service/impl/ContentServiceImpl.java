package cn.e3mall.content.service.impl;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper tbContentMapper;

    @Override
    public EasyUIDataGridResult getContentList(Long categoryId, int page, int row) {
            PageHelper.startPage(page,row);
            TbContentExample tbContentExample = new TbContentExample();
        TbContentExample.Criteria criteria = tbContentExample.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        List<TbContent> tbContents = tbContentMapper.selectByExample(tbContentExample);
            EasyUIDataGridResult result = new EasyUIDataGridResult();
            result.setRows(tbContents);
             PageInfo<TbContent> pageInfo = new PageInfo<>(tbContents);
            result.setTotal((int) pageInfo.getTotal());
        return result;

    }

    @Override
    public E3Result addContent(TbContent tbContent) {
        tbContentMapper.insertSelective(tbContent);

        return E3Result.ok();
    }

    @Override
    public E3Result updateContent(TbContent tbContent) {
        tbContentMapper.updateByPrimaryKeyWithBLOBs(tbContent);
        return E3Result.ok();
    }
}
