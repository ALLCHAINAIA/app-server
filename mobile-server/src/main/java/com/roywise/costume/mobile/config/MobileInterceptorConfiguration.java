package com.roywise.costume.mobile.config;

import com.roywise.common.spring.interceptor.AbstractAuthInterceptor;
import com.roywise.common.spring.service.SpringContextHolder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @description:
 * @date: 2018/3/22 10:56
 */
@Log4j2
@Configuration
@ConfigurationProperties(prefix = "roywise.mobile")
@Setter @Getter
public class MobileInterceptorConfiguration {

    private String interceptorName;

    @Resource
    private SpringContextHolder springContextHolder;


    @SuppressWarnings("static-access")
    @Bean(name = "abstractAuthInterceptor")
    public AbstractAuthInterceptor getInterceptor() {
        log.debug("en" + interceptorName);
        return springContextHolder.getBean(interceptorName);
    }
}
