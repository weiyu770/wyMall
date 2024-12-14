package com.hmall.trade;

import com.hamll.api.client.CartClient;
import com.hamll.api.client.ItemClient;
import com.hamll.api.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.hmall.trade.mapper")
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {CartClient.class, ItemClient.class}, defaultConfiguration = DefaultFeignConfig.class)
public class TradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradeApplication.class, args);
    }
}