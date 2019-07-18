package com.makersy.vo;

import com.makersy.domain.Goods;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by makersy on 2019
 */

@Getter
@Setter
//同时包含商品信息和秒杀信息
public class GoodsVo extends Goods {

    private Double miaoshaPrice;

    private Integer stockCount;

    private Date startDate;

    private Date endDate;

}
