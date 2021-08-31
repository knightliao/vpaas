package com.github.knightliao.vpaas.demos.demo.service.impl;

import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.knightliao.vpaas.demos.demo.service.ClientSimulationAdvService;
import com.github.knightliao.vpaas.demos.demo.service.ClientSimulationService;
import com.github.knightliao.vpaas.demos.demo.service.helper.ClientPingServiceHelper;
import com.github.knightliao.vpaas.demos.demo.service.helper.ClientReconnectServiceHelper;
import com.github.knightliao.vpaas.demos.demo.service.helper.ClientSimulationServiceImpl;
import com.github.knightliao.vpaas.demos.demo.support.dto.ClientDemoContext;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/29 12:49
 */
@Slf4j
@Data
public class ClientSimulationAdvServiceImpl implements ClientSimulationAdvService {

    private ClientDemoContext clientDemoContext;

    private ClientSimulationService clientSimulationService;
    private ClientPingServiceHelper clientPingServiceHelper;
    private ClientReconnectServiceHelper clientReconnectServiceHelper;

    //
    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1000);

    public ClientSimulationAdvServiceImpl(ClientDemoContext clientDemoContext) {
        this.clientDemoContext = clientDemoContext;
        this.clientSimulationService = new ClientSimulationServiceImpl(clientDemoContext);
        this.clientReconnectServiceHelper =
                new ClientReconnectServiceHelper(clientDemoContext, clientSimulationService);
        this.clientPingServiceHelper = new ClientPingServiceHelper(clientDemoContext, clientSimulationService,
                clientReconnectServiceHelper);
    }

    @Override
    public SocketAddress newOne(String serverListStr) {

        try {

            try {
                clientSimulationService.connect(serverListStr);
            } catch (Exception ex) {

                // 重连直到成功
                clientReconnectServiceHelper.reconnectTillSuccessWhenNewClient();
            }

            // 采用线程池来执行ping, 可以有效减少线程量
            executorService.scheduleWithFixedDelay(new PingCallableA(),
                    15000,
                    100,
                    TimeUnit.MILLISECONDS);

            return clientSimulationService.getCurTargetServer();

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            return null;
        }
    }

    class PingCallableA implements Runnable {

        @Override
        public void run() {
            clientPingServiceHelper.run();
        }
    }

    @Override
    public void disconnect() {

        clientSimulationService.sendDisconnectCmd();
    }

    @Override
    public boolean isTcpConnected() {
        return clientSimulationService.isTcpConnected();
    }

    @Override
    public boolean isMqttConnected() {
        return clientSimulationService.isMqttProtocolRealConnected();
    }

    @Override
    public ClientDemoContext getClientDemoContext() {
        return clientDemoContext;
    }
}
