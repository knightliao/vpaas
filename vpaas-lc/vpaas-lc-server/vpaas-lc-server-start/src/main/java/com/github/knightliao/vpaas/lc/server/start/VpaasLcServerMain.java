package com.github.knightliao.vpaas.lc.server.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import lombok.extern.slf4j.Slf4j;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class},
        scanBasePackages = {"com.github.knightliao.vpaas"})
@Slf4j
public class VpaasLcServerMain {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {

        SpringApplication.run(VpaasLcServerMain.class, args);

        log.info("VpaasLcServerMain start success");
    }
}
