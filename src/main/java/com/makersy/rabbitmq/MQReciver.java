package com.makersy.rabbitmq;

import com.makersy.domain.MiaoshaOrder;
import com.makersy.domain.MiaoshaUser;
import com.makersy.redis.RedisService;
import com.makersy.result.CodeMsg;
import com.makersy.result.Result;
import com.makersy.service.GoodsService;
import com.makersy.service.MiaoshaService;
import com.makersy.service.MiaoshaUserService;
import com.makersy.service.OrderService;
import com.makersy.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by makersy on 2019
 */

@Service
@Slf4j
public class MQReciver {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(String message) {
        log.info("receive message" + message);
        MiaoshaMessage mm = RedisService.stringToBean(message, MiaoshaMessage.class);
        MiaoshaUser user = mm.getUser();
        long goodsId = mm.getGoodsId();

        //判断商品是否还有库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
            return;
        }

        //判断是否已经秒杀到商品
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return;
        }

        //减库存，生成订单，向缓存中写入秒杀订单
        miaoshaService.miaosha(user, goods);

    }

//    @RabbitListener(queues = MQConfig.QUEUE)
//    public void receive(String message) {
//        log.info("receive message" + message);
//    }
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
//    public void receiveTopicQueue1(String message) {
//        log.info("receive topic queue1 message" + message);
//    }
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
//    public void receiveTopicQueue2(String message) {
//        log.info("receive topic queue2 message" + message);
//    }
}
