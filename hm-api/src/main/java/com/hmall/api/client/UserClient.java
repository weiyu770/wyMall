package com.hmall.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description:
 * @class: UserClient
 * @author: yu_wei
 * @create: 2024/12/14 15:29
 */
@FeignClient(name = "user-service", path = "/users")  // 声明FeignClient的名称和路径，与UserController中的@RequestMapping("/user")路径保持一致
public interface UserClient {
    @PutMapping("/money/deduct")
    void deductMoney(@RequestParam("pw") String pw, @RequestParam("amount") Integer amount);
}
