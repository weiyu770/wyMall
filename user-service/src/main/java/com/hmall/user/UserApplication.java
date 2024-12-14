package com.hmall.user;

import com.hamll.api.client.ItemClient;
import com.hamll.api.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.hmall.user.mapper")
@SpringBootApplication
@EnableDiscoveryClient
//@EnableFeignClients(basePackages = "com.hmall.api.client" )
@EnableFeignClients(clients = {ItemClient.class}, defaultConfiguration = DefaultFeignConfig.class)
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}