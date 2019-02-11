package cn.e3mall.content.service.impl;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper tbContentMapper;

    @Autowired
    private JedisClient jedisClient;

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
//缓存同步
        jedisClient.hdel("CONTENT_LIST",tbContent.getCategoryId().toString());
        return E3Result.ok();
    }

    @Override
    public E3Result updateContent(TbContent tbContent) {
        tbContentMapper.updateByPrimaryKeyWithBLOBs(tbContent);
        //缓存同步
        jedisClient.hdel("CONTENT_LIST",tbContent.getCategoryId().toString());
        return E3Result.ok();
    }

    @Override
    public List<TbContent> getContentListByCid(Long cid) {
//        查询缓存
        try{
            String json = jedisClient.hget("CONTENT_LIST", cid + "");
            if (StringUtils.isNotBlank(json)){
                List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
                return list;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        TbContentExample example = new TbContentExample();
        List<TbContent> list = tbContentMapper.selectByExampleWithBLOBs(example);

        try{
            jedisClient.hset("CONTENT_LIST",cid+"", JsonUtils.objectToJson(list));
        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }
}
