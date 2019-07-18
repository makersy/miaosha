package com.makersy.controller;

import com.makersy.domain.User;
import com.makersy.rabbitmq.MQSender;
import com.makersy.redis.RedisService;
import com.makersy.redis.UserKey;
import com.makersy.result.Result;
import com.makersy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by makersy on 2019
 */

@Controller
@RequestMapping("/demo")
@Slf4j
public class SampleController {

    @Autowired
    private UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;
//    @RequestMapping("/redis/get")
//    @ResponseBody
//    public Result<User> dbGet() {
//        redisService.get();
//        return Result.success(user);
//    }

//    @RequestMapping("/mq")
//    @ResponseBody
//    public Result<String> mq() {
//        sender.send("hello world!");
//        return Result.success("hello world!");
//    }
//
//    @RequestMapping("/mq/topic")
//    @ResponseBody
//    public Result<String> topic() {
//        sender.sendTopic("hello world!");
//        return Result.success("hello world!");
//    }

    @RequestMapping("/hello")
    public String thymeleaf(Model model) {
        model.addAttribute("name", "makersy");
        log.info("go into /hello: {}");
        return "hello";
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
        String key = "1";
        User value = redisService.get(UserKey.getById, key, User.class);
        return Result.success(value);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
        User user = new User();
        user.setId(1);
        user.setName("11111");
        redisService.set(UserKey.getById, "" + 1, user);
        return Result.success(true);
    }
}
