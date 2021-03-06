package com.github.knightliao.vpaas.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.github.knightliao.star.starter.boot.health.helper.annotation.MyGracefulOnline;

import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/29 01:31
 */
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class},
        scanBasePackages = {"com.github.knightliao.vpaas"})
@MyGracefulOnline(port = 9030)
@Slf4j
public class VpaasProcessorApplication {

    public static void main(String[] args) {

        SpringApplication.run(VpaasProcessorApplication.class, args);

        log.info(VpaasProcessorApplication.class.getName() + " start success");
    }
}
