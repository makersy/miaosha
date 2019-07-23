package com.makersy.controller;

import com.makersy.access.AccessLimit;
import com.makersy.domain.MiaoshaOrder;
import com.makersy.domain.MiaoshaUser;
import com.makersy.domain.OrderInfo;
import com.makersy.rabbitmq.MQSender;
import com.makersy.rabbitmq.MiaoshaMessage;
import com.makersy.redis.*;
import com.makersy.result.CodeMsg;
import com.makersy.result.Result;
import com.makersy.service.GoodsService;
import com.makersy.service.MiaoshaService;
import com.makersy.service.MiaoshaUserService;
import com.makersy.service.OrderService;
import com.makersy.utils.MD5Util;
import com.makersy.utils.UUIDUtil;
import com.makersy.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by makersy on 2019
 */

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    private static Logger log = LoggerFactory.getLogger(MiaoshaController.class);

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

    @Autowired
    MQSender sender;

    // 标记某 id 商品秒杀是否结束
    private Map<Long, Boolean> localOverMap = new HashMap<>();

    /**
     * 系统初始化。需要继承 InitializingBean 来实现此方法。其会在系统初始化时调用。
     * 目的：redis中加载商品数量，更新商品的秒杀结束标志。
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if (goodsVoList == null) {
            return;
        }
        log.info("Init nums of goods");
        //加载每个商品的数量到缓存
        for (GoodsVo goodsVo : goodsVoList) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goodsVo.getId(), goodsVo.getStockCount());
            //
            localOverMap.put(goodsVo.getId(), false);
        }
    }

    @RequestMapping(value="/reset", method=RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(Model model) {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        for(GoodsVo goods : goodsList) {
            goods.setStockCount(10);
            redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), 10);
            localOverMap.put(goods.getId(), false);
        }
        redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
        redisService.delete(MiaoshaKey.isGoodsOver);
        miaoshaService.reset(goodsList);
        return Result.success(true);
    }

    /**
     * GET/POST区别：
     * get具有幂等性，代表从服务端获取数据，无论获取多少次，结果都没有变化，对服务端的数据也没有任何影响
     * post不具有幂等性，代表向服务端提交数据
     */
    /**
     * 1. QPS: 1220.7/sec  win idea
     *    5000 * 10
     *
     */
    @RequestMapping(value = "/{path}/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoshaUser user, @RequestParam("goodsId")long goodsId, @PathVariable("path")String path) {
        //model中设置user对象
        model.addAttribute("user", user);

        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //验证path
        boolean check = miaoshaService.checkPath(user, goodsId, path);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGEL);
        }

        /*
        用来对 redis 进行优化，当商品已经秒杀完后，对应“秒杀结束”状态值变为 true，此时直接返回失败，
        不会再进行查询 redis，以及入队操作
         */
        boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }

        //预减库存
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.replace(goodsId, true);
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }

        //判断是否已经秒杀到
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        //入队
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        sender.sendMiaoshaMessage(mm);
        //排队中
        return Result.success(0);

        //todo 要解决一个用户秒杀到两个商品的问题
        /*
        //判断商品是否还有库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }

        //判断是否已经秒杀到商品
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        //如果满足秒杀条件
        //事务：减库存，下订单，写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        if (orderInfo == null) {
            return Result.error(CodeMsg.MIAOSHA_OVER);
        } else {
            return Result.success(orderInfo);
        }
        */
    }

    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request, MiaoshaUser user,
                                         @RequestParam("goodsId")long goodsId,
                                         @RequestParam(value = "verifyCode", defaultValue = "0")int verifyCode){
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //查询访问的次数
        String uri = request.getRequestURI();
        String key = uri + "_" + user.getId();

        //检验验证码
        boolean check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGEL);
        }

        //生成路径
        String path = miaoshaService.createMiaoshaPath(user, goodsId);
        return Result.success(path);
    }


    /**
        orderId : 成功
        -1 : 秒杀失败
        0 : 排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, MiaoshaUser user, @RequestParam("goodsId")long goodsId) {
        //model中设置user对象
        model.addAttribute("user", user);

        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }

    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> getMiaoshaVerifyCode(HttpServletResponse response, Model model, MiaoshaUser user, @RequestParam("goodsId")long goodsId) {
        //model中设置user对象
        model.addAttribute("user", user);

        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        BufferedImage image = miaoshaService.createVerifyCode(user, goodsId);
        try {
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        } catch (IOException e) {
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }


}
