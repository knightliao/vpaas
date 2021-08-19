package com.github.knightliao.vpaas.common.rely.config.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/18 15:56
 */
@EnableConfigurationProperties
@PropertySource("classpath:vpaas.properties")
@Slf4j
@Configuration
@EnableCaching
@Data
public class VpaasConfig {

    @Value("${vpaas.server.brokerId:0}")
    private int serverBrokerId;

    @Value("${vpaas.server.port:6000}")
    private int serverPort;

    @Value("${vpaas.server.status.port:6001}")
    private int serverStatusPort;

}