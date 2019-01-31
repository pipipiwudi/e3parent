package cn.e3mall.service.impl;

import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.service.ItemService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public TbItem getItemById(Long itemId) {
//        根据主键查询
//        return itemMapper.selectByPrimaryKey(itemId);
//    根据条件查询
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(itemId);
        List<TbItem> tbItems = itemMapper.selectByExample(example);

        if (tbItems!=null && tbItems.size()>0)
        {
            return tbItems.get(0);
        }
        return null;
    }

    @Override
    public String getNameById(Long itemId) {
        String s = itemMapper.selectTest(itemId);
        return s;
    }

    @Override
    public EasyUIDataGridResult getItemList(int page, int rows) {
//        设置分页信息
        PageHelper.startPage(page,rows);
//        执行查询
        TbItemExample tbItemExample = new TbItemExample();
        List<TbItem> list = itemMapper.selectByExample(tbItemExample);
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        result.setRows(list);

        PageInfo<TbItem> pageInfo = new PageInfo<>(list);
        result.setTotal((int) pageInfo.getTotal());
        return result;
    }

}
