package com.github.knightliao.vpaas.demos.demo.demo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.github.knightliao.vpaas.demos.demo.service.ClientSimulationService;
import com.github.knightliao.vpaas.demos.demo.service.impl.ClientSimulationEclipseImpl;
import com.github.knightliao.vpaas.demos.demo.support.dto.ClientDemoContext;
import com.github.knightliao.vpaas.demos.demo.support.enums.ClientRunEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/29 11:27
 */
@Slf4j
public class VpaasClientDemoEclipse {

    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1000);

    public static void main(String[] args) {

        try {

            String clientId = "GID_XXX@@CLientID_123";
            Long uid = 100899712L;

            ClientDemoContext clientDemoContext =
                    ClientDemoContext.builder().clientId(clientId)
                            .uid(uid)
                            // 10秒一次心跳
                            .keepAliveTimeoutSecond(10)
                            .clientRunEnum(ClientRunEnum.SINGLE).build();

            ClientSimulationService clientSimulationService = new ClientSimulationEclipseImpl(clientDemoContext,
                    scheduledExecutorService);

            clientSimulationService.connect("127.0.0.1:6000");

            while (true) {
                Thread.sleep(5000);
            }

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        System.exit(0);
    }
}
