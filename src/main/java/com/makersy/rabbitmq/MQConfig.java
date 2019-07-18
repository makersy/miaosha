package com.makersy.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by makersy on 2019
 */

@Configuration
public class MQConfig {

    public static final String QUEUE = "queue";
    public static final String MIAOSHA_QUEUE = "miaosha_queue";
    public static final String TOPIC_QUEUE1 = "topic_queue1";
    public static final String TOPIC_QUEUE2 = "topic_queue2";
    public static final String TOPIC_EXCHANGE = "topicExchange";

    /**
     * Direct模式 交换机Exchange
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);  //作持久化
    }

    @Bean
    public Queue miaoshaQueue() {
        return new Queue(MIAOSHA_QUEUE, true);
    }

    /**
     * Topic模式 交换机Exchange
     */
    @Bean
    public Queue topicQueue1() {
        return new Queue(TOPIC_QUEUE1, true);  //作持久化
    }
    @Bean
    public Queue topicQueue2() {
        return new Queue(TOPIC_QUEUE2, true);  //作持久化
    }
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    /**
     * 将交换机和 Key 进行了绑定
     */
    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with("topic.key1");
    }

    @Bean
    public Binding topicBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with("topic.#");
    }
}
