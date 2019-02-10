package cn.e3mall.content.service.impl;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCategoryService;
import cn.e3mall.mapper.TbContentCategoryMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentCategory;
import cn.e3mall.pojo.TbContentCategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

    @Autowired
    private TbContentCategoryMapper contentCategoryMapper;

    @SuppressWarnings("Duplicates")
    @Override
    public List<EasyUITreeNode> getCategoryList(Long parentId) {

        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);

        List<EasyUITreeNode> result = new ArrayList();
        for (TbContentCategory c:list
             ) {
            EasyUITreeNode node = new EasyUITreeNode();
            node.setId(c.getId());
            node.setText(c.getName());
            node.setState(c.getIsParent()?"closed":"open");
            result.add(node);
        }
        return result;
    }

    @Override
    public E3Result addContentCategory(Long parentId, String name) {
        //创建一个tb_content_category表对应的pojo对象
        TbContentCategory tbContentCategory = new TbContentCategory();
        //设置pojo的属性
        tbContentCategory.setParentId(parentId);
        tbContentCategory.setName(name);

        //1(正常),2(删除)
        tbContentCategory.setStatus(1);
        //默认排序就是1
        tbContentCategory.setSortOrder(1);
        //新添加的节点一定是叶子节点
        tbContentCategory.setIsParent(false);
        tbContentCategory.setCreated(new Date());
        tbContentCategory.setUpdated(new Date());
        //插入到数据库
        contentCategoryMapper.insert(tbContentCategory);
        //判断父节点的isparent属性。如果不是true改为true
        TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
        if (!parent.getIsParent()){
            parent.setIsParent(true);
            contentCategoryMapper.updateByPrimaryKey(parent);
        }
        return E3Result.ok(tbContentCategory);
    }

    @Override
    public E3Result updateContentCategory(Long id, String name) {

        TbContentCategory tbContentCategory = contentCategoryMapper.selectByPrimaryKey(id);
        tbContentCategory.setName(name);
        tbContentCategory.setUpdated(new Date());
        contentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);

        return E3Result.ok();
    }

    @Override
    public E3Result deleteContentCategory(Long id) {
        contentCategoryMapper.deleteByPrimaryKey(id);
        return E3Result.ok();
    }
}
