package com.github.knightliao.vpaas.demos.demo.service.impl;

import java.net.SocketAddress;

import com.github.knightliao.vpaas.demos.demo.service.ClientSimulationService;
import com.github.knightliao.vpaas.demos.demo.service.helper.ClientSimulationBaseService;
import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/29 01:43
 */
public class ClientSimulationServiceImpl extends ClientSimulationBaseService implements ClientSimulationService {

    @Override
    public LcWrappedChannel getChannel() {
        return null;
    }

    @Override
    public boolean connect(String serverListStr) {
        return false;
    }

    @Override
    public SocketAddress getCurTargetServer() {
        return null;
    }

    @Override
    public void login() {

    }

    @Override
    public void login(long uid) {

    }

    @Override
    public boolean reconnect() {
        return false;
    }

    @Override
    public void sendDisconnectCmd() {

    }

    @Override
    public void doPubAck(int messageId) {

    }

    @Override
    public void ping() {

    }

    @Override
    public void setConnectStatus(String scene, boolean connectOk, boolean isAuth) {

    }

    @Override
    public void setToken(String token) {

    }

    @Override
    public boolean isMqttProtocolRealConnected() {
        return false;
    }

    @Override
    public boolean TcpConnected() {
        return false;
    }
}
