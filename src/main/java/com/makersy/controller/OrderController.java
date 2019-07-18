package com.makersy.controller;

import com.makersy.domain.MiaoshaUser;
import com.makersy.domain.OrderInfo;
import com.makersy.redis.GoodsKey;
import com.makersy.redis.RedisService;
import com.makersy.result.CodeMsg;
import com.makersy.result.Result;
import com.makersy.service.GoodsService;
import com.makersy.service.MiaoshaUserService;
import com.makersy.service.OrderService;
import com.makersy.vo.GoodsDetailVo;
import com.makersy.vo.GoodsVo;
import com.makersy.vo.OrderDetaiVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by makersy on 2019
 */

@Controller
@RequestMapping("/order")
public class OrderController {

    private static Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 通过用户id获取订单信息
     * @param user
     * @param orderId
     * @return 含有订单信息OrderInfo，和商品信息Goods
     */
    @RequestMapping("/detail")
    @ResponseBody
    //todo 拦截器needLogin，避免判断user是否为空
    public Result<OrderDetaiVo> info(MiaoshaUser user, @RequestParam("orderId") long orderId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        //如果订单不存在
        if (order == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        //获取商品信息
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetaiVo orderDetailVo = new OrderDetaiVo();
        orderDetailVo.setOrder(order);
        orderDetailVo.setGoods(goods);
        return Result.success(orderDetailVo);
    }
}
