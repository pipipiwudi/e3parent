package cn.e3mall.order.service.impl;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.mapper.TbOrderItemMapper;
import cn.e3mall.mapper.TbOrderMapper;
import cn.e3mall.mapper.TbOrderShippingMapper;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbOrderItem;
import cn.e3mall.pojo.TbOrderShipping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private TbOrderShippingMapper orderShippingMapper;

    @Autowired
    private JedisClient jedisClient;

    @Override
    public E3Result createOrder(OrderInfo orderInfo) {

        if (! jedisClient.exists("ORDER_ID_GEN")){
            jedisClient.set("ORDER_ID_GEN","100544");
        }
        String  order_id_gen = jedisClient.incr("ORDER_ID_GEN").toString();

        orderInfo.setOrderId(order_id_gen);
        orderInfo.setStatus(1);
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());

        orderMapper.insert(orderInfo);

        List<TbOrderItem> orderItems = orderInfo.getOrderItems();
        for (TbOrderItem orderItem :orderItems
             ) {
            String order_detail_id_gen = jedisClient.incr("ORDER_DETAIL_ID_GEN").toString();
            orderItem.setId(order_detail_id_gen);
            orderItem.setOrderId(order_id_gen);

            orderItemMapper.insert(orderItem);
        }

        TbOrderShipping orderShipping = orderInfo.getOrderShipping();
        orderShipping.setOrderId(order_id_gen);
        orderShipping.setCreated(new Date());
        orderShipping.setUpdated(new Date());

        orderShippingMapper.insert(orderShipping);

//        清空购物车
        return E3Result.ok(order_id_gen);
    }
}
