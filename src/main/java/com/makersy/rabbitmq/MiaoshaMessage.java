package com.makersy.rabbitmq;

import com.makersy.domain.MiaoshaUser;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by makersy on 2019
 */

@Getter
@Setter
public class MiaoshaMessage {

    private MiaoshaUser user;

    private long goodsId;

}
