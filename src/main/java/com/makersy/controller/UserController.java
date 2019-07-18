package com.makersy.controller;

import com.makersy.domain.MiaoshaUser;
import com.makersy.redis.RedisService;
import com.makersy.result.Result;
import com.makersy.service.GoodsService;
import com.makersy.service.MiaoshaUserService;
import com.makersy.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by makersy on 2019
 */

@Controller
@RequestMapping("/user")
public class UserController {


    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;



    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> toLogin(Model model, MiaoshaUser user) {

        return Result.success(user);
    }

}
