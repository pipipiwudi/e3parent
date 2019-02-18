package cn.e3mall.cart.service.impl;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private JedisClient jedisClient;
    @Autowired
    private TbItemMapper tbItemMapper;

    @Override
    public E3Result addCart(long userId, long itemId, int num) {

        String s = jedisClient.hget("CART:" + userId, itemId + "");
        if (StringUtils.isNotBlank(s)){
            TbItem item = JsonUtils.jsonToPojo(s, TbItem.class);
            item.setNum(item.getNum()+num);

            jedisClient.hset("CART:" + userId, itemId + "",JsonUtils.objectToJson(item));
            return E3Result.ok();
        }

        TbItem item = tbItemMapper.selectByPrimaryKey(itemId);
        item.setNum(num);
        String image = item.getImage();
        if (StringUtils.isNotBlank(image)){
            item.setImage(image.split(",")[0]);
        }
        jedisClient.hset("CART:" + userId, itemId + "",JsonUtils.objectToJson(item));
        return E3Result.ok();

    }

    @Override
    public E3Result mergeCart(long userId, List<TbItem> itemList) {
        for (TbItem tbitem: itemList
             ) {
            addCart(userId,tbitem.getId(),tbitem.getNum());
        }
        return E3Result.ok();
    }

    @Override
    public List<TbItem> getCartList(long userId) {
        List<String> jsonlist = jedisClient.hvals("CART:" + userId);

        List<TbItem> itemList = new ArrayList<>();

        for (String string: jsonlist
             ) {
            TbItem item = JsonUtils.jsonToPojo(string, TbItem.class);
            itemList.add(item);
        }
        return itemList;
    }

    @Override
    public E3Result updateCartNum(long userId, long itemId, int num) {
        String json = jedisClient.hget("CART:" + userId, itemId + "");
        TbItem item = JsonUtils.jsonToPojo(json, TbItem.class);
        item.setNum(num);
        jedisClient.hset("CART:" + userId, itemId + "",JsonUtils.objectToJson(item));
        return E3Result.ok();
    }

    @Override
    public E3Result deleteCartItem(long userId, long itemId) {
        jedisClient.hdel("CART:" + userId, itemId + "");

        return E3Result.ok();
    }

    @Override
    public E3Result clearCartItem(long userId) {
        jedisClient.del("CART:" + userId);
        return E3Result.ok();
    }
}
