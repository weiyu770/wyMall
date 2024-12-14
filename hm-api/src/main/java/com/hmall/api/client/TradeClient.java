package com.hmall.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * @description:
 * @class: UserClient
 * @author: yu_wei
 * @create: 2024/12/14 15:29
 */
@FeignClient(name = "trade-service", path = "/orders")  // 声明FeignClient的名称和路径，与UserController中的@RequestMapping("/orders")路径保持一致
public interface TradeClient {
    /**
     * 标记订单为已支付
     * @param orderId
     */
    @PutMapping("/{orderId}")
    public void markOrderPaySuccess(@PathVariable("orderId") Long orderId);
}
