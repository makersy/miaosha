package com.makersy.service;


import com.makersy.dao.MiaoshaUserDao;
import com.makersy.domain.MiaoshaOrder;
import com.makersy.domain.MiaoshaUser;
import com.makersy.domain.OrderInfo;
import com.makersy.redis.MiaoshaKey;
import com.makersy.redis.RedisService;
import com.makersy.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@EnableTransactionManagement
public class MiaoshaService {

	//service中只能调用自己的dao，以及其他的service。不能调用其他service

	@Autowired
	private GoodsService goodsService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private RedisService redisService;

	@Transactional
	public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
		//减库存
		boolean success = goodsService.reduceStock(goods);
		//生成并写入订单，返回生成的订单信息
		if (success) {
			return orderService.createOrder(user, goods);
		} else {
			setGoodsOver(goods.getId());
			return null;
		}
	}

	public long getMiaoshaResult(Long userId, long goodsId) {
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
		if (order != null) {  //秒杀成功
			return order.getOrderId();
		} else {
			boolean isOver = getGoodsOver(goodsId);
			if (isOver) {  //卖完了
				return -1;
			} else {
				return 0;
			}

		}
	}

	private void setGoodsOver(Long goodsId) {
		redisService.set(MiaoshaKey.isGoodsOver, "" + goodsId, true);
	}

	private boolean getGoodsOver(long goodsId) {
		return redisService.exists(MiaoshaKey.isGoodsOver, "" + goodsId);
	}

	public void reset(List<GoodsVo> goodsList) {
		goodsService.resetStock(goodsList);
		orderService.deleteOrders();
	}
}
