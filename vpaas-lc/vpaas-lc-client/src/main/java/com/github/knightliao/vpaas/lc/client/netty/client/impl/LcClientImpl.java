package com.github.knightliao.vpaas.lc.client.netty.client.impl;

import java.net.SocketAddress;

import com.github.knightliao.vpaas.lc.client.netty.client.ILcClient;
import com.github.knightliao.vpaas.lc.client.support.dto.LcClientParam;

import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @date 2021/8/8 17:58
 */
@Slf4j
public class LcClientImpl extends BaseClient implements ILcClient {

    @Override
    public LcClientParam getParam() {
        return null;
    }

    @Override
    public ChannelFuture connect() {
        return null;
    }

    @Override
    public SocketAddress getCurTargetServer() {
        return null;
    }

    @Override
    public void reconnect() {

    }

    @Override
    public void setErrorFlag() {

    }

    @Override
    public ChannelFuture connect(boolean sync) {
        return null;
    }
}
