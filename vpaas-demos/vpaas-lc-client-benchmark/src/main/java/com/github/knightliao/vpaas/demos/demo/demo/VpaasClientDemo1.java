package com.github.knightliao.vpaas.demos.demo.demo;

import org.apache.commons.lang3.RandomUtils;

import com.github.knightliao.vpaas.demos.demo.service.ClientSimulationAdvService;
import com.github.knightliao.vpaas.demos.demo.service.impl.ClientSimulationAdvServiceImpl;
import com.github.knightliao.vpaas.demos.demo.support.dto.ClientDemoContext;
import com.github.knightliao.vpaas.demos.demo.support.enums.ClientRunEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/29 12:47
 */
@Slf4j
public class VpaasClientDemo1 {

    public static void main(String[] args) {

        try {

            String clientId = "GID_XXX@@CLientID_123";
            Long uid = 100899712L;
            String ip = "127.0.0.1:6000";

            ClientDemoContext clientDemoContext =
                    ClientDemoContext.builder().clientId(clientId)
                            .uid(uid)
                            // 10秒一次心跳
                            .keepAliveTimeoutSecond(10)
                            .loginPerhaps(2)
                            .logoutPerhaps(2)
                            .disconnectPerhaps(0)
                            .clientRunEnum(ClientRunEnum.SINGLE).build();

            ClientSimulationAdvService clientSimulationAdvService =
                    new ClientSimulationAdvServiceImpl(clientDemoContext);
            clientSimulationAdvService.newOne(ip);

            while (true) {
                Thread.sleep(5000);

                if (RandomUtils.nextInt() % 1000 == 1) {
                    clientSimulationAdvService.disconnect();
                    break;
                }
            }

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        System.exit(0);
    }
}
