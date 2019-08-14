package com.orz.huanhuan.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hesher
 */
@Getter
@AllArgsConstructor
public enum HuanhuanOrderStatus {

    /**
     * 等待发货
     */
    WAIT_TO_SHIP("已确认,已付款,未发货"),

    /**
     * 发货中
     */
    SHIPPING("已确认,已付款,发货中");

    private final String desc;
}
