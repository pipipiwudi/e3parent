package cn.e3mall.service.impl;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.mapper.TbItemParamMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.pojo.TbItemParam;
import cn.e3mall.service.ItemService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Date;
import java.util.List;


@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemDescMapper itemDescMapper;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Resource
    private Destination topicDestination;

    @Autowired
    private JedisClient jedisClient;
    @Autowired
    private TbItemParamMapper itemParamMapper;
    @Override
    public TbItem getItemById(Long itemId) {
//        根据主键查询
//        return itemMapper.selectByPrimaryKey(itemId);
//        查询缓存
        try {
            String json = jedisClient.get("ITEM_INFO:" + itemId + ":BASE");
            if (StringUtils.isNotBlank(json)){
                TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
                return tbItem;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
//    根据条件查询 数据库
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(itemId);
        List<TbItem> tbItems = itemMapper.selectByExample(example);

        if (tbItems!=null && tbItems.size()>0)
        {
//            添加缓存
            try {
            jedisClient.set("ITEM_INFO:"+itemId+":BASE",JsonUtils.objectToJson(tbItems.get(0)));
//            设置过期时间
                jedisClient.expire("ITEM_INFO:"+itemId+":BASE",3600);
            }catch (Exception e){
                e.printStackTrace();
            }
            return tbItems.get(0);
        }
        return null;
    }

    @Override
    public String getNameById(Long itemId) {
        return null;
    }


    @Override
    public TbItemDesc getItemDescById(Long itemId) {
//        查询缓存
        try {
            String json = jedisClient.get("ITEM_INFO:" + itemId + ":DESC");
            if (StringUtils.isNotBlank(json)){
                TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
                return tbItemDesc;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
        //            添加缓存
        try {
            jedisClient.set("ITEM_INFO:"+itemId+":DESC",JsonUtils.objectToJson(itemDesc));
//            设置过期时间
            jedisClient.expire("ITEM_INFO:"+itemId+":DESC",3600);
        }catch (Exception e){
            e.printStackTrace();
        }
        return itemDesc;
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

    @Override
    public E3Result addItem(TbItem item, String desc) {

//        生成商品id
        final long id = IDUtils.genItemId();
        item.setId(id);
//        1-正常 2-下架 3-删除
        item.setStatus((byte) 1);
        item.setCreated(new Date());
        item.setUpdated(new Date());
        itemMapper.insert(item);

        TbItemDesc itemDesc = new TbItemDesc();
        itemDesc.setItemId(item.getId());
        itemDesc.setItemDesc(desc);
        itemDesc.setCreated(new Date());
        itemDesc.setUpdated(new Date());
        itemDescMapper.insert(itemDesc);
        jmsTemplate.send(topicDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(id + "");
                return textMessage;
            }
        });
        return E3Result.ok();
    }

    @Override
    public E3Result getItemDesc(Long itemId) {
        TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
        String desc = itemDesc.getItemDesc();
        return new  E3Result(desc);

    }

    @Override
    public E3Result updateItem(TbItem item, String desc) {
        //        1-正常 2-下架 3-删除

        TbItem tbItem = this.getItemById(item.getId());
        item.setStatus(tbItem.getStatus());
        item.setUpdated(new Date());
        item.setCreated(tbItem.getCreated());
        itemMapper.updateByPrimaryKey(item);

        TbItemDesc itemDesc = this.getItemDescById(item.getId());
        itemDesc.setItemDesc(desc);
        itemDesc.setUpdated(new Date());
        itemDescMapper.updateByPrimaryKeyWithBLOBs(itemDesc);

        return E3Result.ok();
    }

    @Override
    public E3Result deleteItem(String[] itemIds) {
        for (String itemId:itemIds
             ) {
            itemMapper.deleteByPrimaryKey(Long.parseLong(itemId));
        }
        return E3Result.ok();
    }

//    @Override
//    public E3Result getItemParam(Long itemId) {
//        TbItemParam itemParam = itemParamMapper.selectByPrimaryKey(itemId);
//        String paramData = itemParam.getParamData();
//        return new E3Result(paramData);
//    }


}
