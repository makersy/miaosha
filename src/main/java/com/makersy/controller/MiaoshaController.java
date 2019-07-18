package com.makersy.controller;

import com.makersy.domain.MiaoshaOrder;
import com.makersy.domain.MiaoshaUser;
import com.makersy.domain.OrderInfo;
import com.makersy.rabbitmq.MQSender;
import com.makersy.rabbitmq.MiaoshaMessage;
import com.makersy.redis.GoodsKey;
import com.makersy.redis.RedisService;
import com.makersy.result.CodeMsg;
import com.makersy.result.Result;
import com.makersy.service.GoodsService;
import com.makersy.service.MiaoshaService;
import com.makersy.service.MiaoshaUserService;
import com.makersy.service.OrderService;
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

import java.util.List;

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


    /**
     * 继承 InitializingBean 来实现此方法。其会在系统初始化时调用。系统初始化时加载商品数量
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
        }
    }

    /**
     * GET/POST区别：
     * get具有幂等性，代表从服务端获取数据，无论获取多少次，结果都没有变化，对服务端的数据也没有任何影响
     * post不具有幂等性，代表向服务端提交数据
     */
    @RequestMapping(value = "/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoshaUser user, @RequestParam("goodsId")long goodsId) {
        //model中设置user对象
        model.addAttribute("user", user);

        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //预减库存
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0) {
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
}
