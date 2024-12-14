package com.hmall.api.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * 日志记录
 */
public class DefaultFeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
