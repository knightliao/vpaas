package com.github.knightliao.vpaas.demos.demo.service.helper;

import com.github.knightliao.vpaas.demos.demo.service.ClientSimulationService;
import com.github.knightliao.vpaas.demos.demo.support.dto.ClientDemoContext;
import com.github.knightliao.vpaas.demos.demo.support.log.DemoLogUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/29 12:52
 */
@Slf4j
public class ClientReconnectServiceHelper {

    private ClientDemoContext clientDemoContext;
    private ClientSimulationService clientSimulationService;
    private int reconnectTimes = 0;

    public ClientReconnectServiceHelper(ClientDemoContext clientDemoContext,
                                        ClientSimulationService clientSimulationService) {
        this.clientDemoContext = clientDemoContext;
        this.clientSimulationService = clientSimulationService;
    }

    // 重连直至成功，顺序执行，限制并发
    public synchronized void reconnectTillSuccessWhenNewClient() {

        while (true) {

            // 没有连接成功，则重连
            if (!clientSimulationService.isMqttProtocolRealConnected()) {

                try {

                    // 先sleep
                    sleep(reconnectTimes);

                    // 回退
                    reconnectTimes++;
                    if (reconnectTimes > 10) {
                        reconnectTimes = 10;
                    }

                    // 重连
                    justReconnect();

                } catch (Exception e) {

                    log.error(e.toString(), e);
                }

            } else {

                // 成功了
                reconnectTimes = 0;
                break;
            }
        }
    }

    private void sleep(int reconnectTimes) {

        int sleepMill = 5000 * reconnectTimes;
        try {
            DemoLogUtils.info(clientDemoContext.getClientRunEnum(), "new_client_do_sleep {0} {1} {2}",
                    clientDemoContext.getClientId(), clientDemoContext.getUid(), sleepMill);

            Thread.sleep(sleepMill);

        } catch (InterruptedException e) {
        }
    }

    public void justReconnect() {

        //
        DemoLogUtils.info(clientDemoContext.getClientRunEnum(), "{0}", "NOT_CONNECTED_WILL_CONNECT");

        // 重连
        clientSimulationService.reconnect();
    }
}
