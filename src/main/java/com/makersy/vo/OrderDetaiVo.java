package com.makersy.vo;

import com.makersy.domain.OrderInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by makersy on 2019
 */

@Getter
@Setter
public class OrderDetaiVo {

    private GoodsVo goods;

    private OrderInfo order;
}
