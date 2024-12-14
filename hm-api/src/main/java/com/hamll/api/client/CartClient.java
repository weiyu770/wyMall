package com.hamll.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

/**
 * @description:
 * @class: CartClient
 * @author: yu_wei
 * @create: 2024/12/14 15:06
 */
@FeignClient(value = "cart-service")
public interface CartClient {
    
    /**
     * 批量删除购物车条目
     * @param ids
     */
    @DeleteMapping("/carts")
    void deleteCartItemByIds(@RequestParam("ids") Collection<Long> ids);
}
