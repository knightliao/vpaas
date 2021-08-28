package com.github.knightliao.vpaas.demos.demo.service;

import java.net.SocketAddress;

import com.github.knightliao.vpaas.demos.demo.support.dto.ClientDemoContext;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/29 01:41
 */
public interface ClientSimulationAdvService {

    SocketAddress newOne(String serverListStr);

    void disconnect();

    boolean isTcpConnected();

    boolean isMqttConnected();

    ClientDemoContext getClientDemoContext();
}
