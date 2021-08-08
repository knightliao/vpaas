package com.github.knightliao.vpaas.lc.client.service.impl;

import java.net.SocketAddress;
import java.util.EventListener;

import com.github.knightliao.vpaas.lc.client.IMyLcClient;
import com.github.knightliao.vpaas.lc.client.netty.client.ILcClient;
import com.github.knightliao.vpaas.lc.client.support.dto.ClientOptions;
import com.github.knightliao.vpaas.lc.client.support.dto.LcClientParam;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.RequestMsg;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.ResponseMsg;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * @author knightliao
 * @date 2021/8/8 23:28
 */
public class MyLcClientImpl implements IMyLcClient {

    private ILcClient lcClient = null;

    @Override
    public Channel getChannel() {
        return lcClient.getChannel();
    }

    @Override
    public void addEventListener(EventListener eventListener) {
        if (lcClient != null) {
            lcClient.addEventListener(eventListener);
        }
    }

    @Override
    public IMyLcClient newClient(ClientOptions clientOptions) {

        return null;
    }

    @Override
    public SocketAddress getCurTargetServer() {
        return null;
    }

    @Override
    public ChannelFuture connect() {
        return null;
    }

    @Override
    public void reconnect() {

    }

    @Override
    public void close() {

    }

    @Override
    public LcClientParam getParam() {
        return null;
    }

    @Override
    public boolean connectWait(int timeoutMs) {
        return false;
    }

    @Override
    public ChannelFuture send(Object message) {
        return null;
    }

    @Override
    public ResponseMsg sendWithSync(RequestMsg requestMsg) {
        return null;
    }

    @Override
    public ResponseMsg sendWithSync(RequestMsg requestMsg, int timeout) {
        return null;
    }
}
