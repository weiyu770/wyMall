package com.hmall.api.config;

import com.hmall.common.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

/**
 * 日志记录 每次微服务调用之前都会来获取当前的登陆的用户信息
 */
public class DefaultFeignConfig {
    
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DefaultFeignConfig.class);
    
    @Bean
    public Logger.Level fullFeignLoggerLevel() {
        return Logger.Level.FULL;
    }
    
    /**
     *
     * @return
     */
    @Bean
    public RequestInterceptor userInfoRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                Long userId = UserContext.getUser();
                log.info("当前登陆的用户为：", userId);
                if (userId != null) {
                    template.header("user-info", userId.toString());
                }
            }
        };
    }
}