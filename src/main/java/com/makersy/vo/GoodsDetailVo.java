package com.makersy.vo;

import com.makersy.domain.Goods;
import com.makersy.domain.MiaoshaUser;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by makersy on 2019
 */

@Getter
@Setter
public class GoodsDetailVo extends Goods {

    private int miaoshaStatus = 0;

    private int remainSeconds = 0;

    private GoodsVo goods;

    private MiaoshaUser user;

}
