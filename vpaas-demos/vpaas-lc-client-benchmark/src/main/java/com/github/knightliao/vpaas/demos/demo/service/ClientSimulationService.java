package com.github.knightliao.vpaas.demos.demo.service;

import java.net.SocketAddress;

import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/29 01:36
 */
public interface ClientSimulationService {

    LcWrappedChannel getChannel();

    boolean connect(String serverListStr);

    SocketAddress getCurTargetServer();

    void login();

    void logout();

    boolean reconnect();

    void sendDisconnectCmd();

    void doPubAck(int messageId);

    void ping();

    void setConnectStatus(String scene, boolean connectOk, boolean isAuth);

    void setToken(String token);

    boolean isMqttProtocolRealConnected();

    boolean isTcpConnected();
}
