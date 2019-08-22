package com.makersy.service;

import com.makersy.dao.GoodsDao;
import com.makersy.dao.OrderDao;
import com.makersy.domain.MiaoshaOrder;
import com.makersy.domain.MiaoshaUser;
import com.makersy.domain.OrderInfo;
import com.makersy.redis.OrderKey;
import com.makersy.redis.RedisService;
import com.makersy.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by makersy on 2019
 */

@Service
@EnableTransactionManagement
public class OrderService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private OrderDao orderDao;

    /**
     * 从缓存获取秒杀订单
     */
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long id, long goodsId) {
        return redisService.get(OrderKey.getMiaoshaOrderByUidGid, id + "_" + goodsId, MiaoshaOrder.class);
    }

    /**
     * 通过orderId获取订单详情
     */
    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }

    /**
     * 下订单，写入秒杀订单
     * @param user
     * @param goods
     * @return 订单信息
     */
    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
        //生成订单
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        //todo 最好用枚举，新建未支付
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());

        //写入订单
        orderDao.insert(orderInfo);

        //将秒杀订单写入缓存
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);
        redisService.set(OrderKey.getMiaoshaOrderByUidGid, "" + user.getId() + "_" + goods.getId(), miaoshaOrder);

        return orderInfo;
    }


    public void deleteOrders() {
        orderDao.deleteOrders();
        orderDao.deleteMiaoshaOrders();
    }
}
