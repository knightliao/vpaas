package com.github.knightliao.vpaas.demos.demo.demo.single;

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
public class VpaasClientDemo2 {

    public static void main(String[] args) {

        try {

            for (long i = 0; i < 50; ++i) {

                String clientId = "GID_XXX@@CLientID_" + i;
                Long uid = i;
                String ip = "127.0.0.1:6000";

                ClientDemoContext clientDemoContext =
                        ClientDemoContext.builder().clientId(clientId)
                                .uid(uid)
                                // 10秒一次心跳
                                .keepAliveTimeoutSecond(10)
                                .loginPerhaps(0)
                                .logoutPerhaps(0)
                                .disconnectPerhaps(0)
                                .clientRunEnum(ClientRunEnum.SINGLE).build();

                ClientSimulationAdvService clientSimulationAdvService =
                        new ClientSimulationAdvServiceImpl(clientDemoContext);
                clientSimulationAdvService.newOne(ip);

                while (true) {
                    Thread.sleep(5000);

                }
            }

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        System.exit(0);
    }
}
