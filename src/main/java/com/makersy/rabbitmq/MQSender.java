package com.makersy.rabbitmq;

import com.makersy.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by makersy on 2019
 */

@Service
@Slf4j
public class MQSender {

    @Autowired
    AmqpTemplate amqpTemplate;

//    //发送消息
//    public void send(Object message) {
//        String msg = RedisService.beanToString(message);  //将对象转化为字符串
//        log.info("send message" + message);
//        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
//    }
//
//    public void sendTopic(Object message) {
//        String msg = RedisService.beanToString(message);  //将对象转化为字符串
//        log.info("send Topic message" + message);
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key1", msg+"1");
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key2", msg+"2");
//    }

    public void sendMiaoshaMessage(MiaoshaMessage mm) {
        String msg = RedisService.beanToString(mm);
        log.info("send Message: " + msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);

    }
}
