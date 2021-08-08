package com.github.knightliao.vpaas.lc.client;

import java.net.SocketAddress;
import java.util.EventListener;

import com.github.knightliao.vpaas.lc.client.support.dto.ClientOptions;
import com.github.knightliao.vpaas.lc.client.support.dto.LcClientParam;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.RequestMsg;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.ResponseMsg;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * @author knightliao
 * @date 2021/8/8 23:22
 */
public interface IMyLcClient {

    Channel getChannel();

    void addEventListener(EventListener eventListener);

    IMyLcClient newClient(ClientOptions clientOptions);

    SocketAddress getCurTargetServer();

    ChannelFuture connect();

    void reconnect();

    void close();

    LcClientParam getParam();

    boolean connectWait(int timeoutMs);

    ChannelFuture send(Object message);

    ResponseMsg sendWithSync(RequestMsg requestMsg);

    ResponseMsg sendWithSync(RequestMsg requestMsg, int timeout);
}
