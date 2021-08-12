package com.github.knightliao.vpaas.lc.client.netty.client;

import java.net.SocketAddress;
import java.util.EventListener;

import com.github.knightliao.vpaas.lc.client.support.dto.LcClientParam;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.RequestMsg;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.ResponseMsg;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/8 17:49
 */
public interface ILcClient {

    Channel getChannel();

    LcClientParam getParam();

    ChannelFuture connect();

    SocketAddress getCurTargetServer();

    void reconnect();

    void close();

    void setErrorFlag();

    void addEventListener(EventListener eventListener);

    ChannelFuture connect(boolean sync);

    ChannelFuture send(Object msg);

    ResponseMsg sendWithSync(RequestMsg message);

    ResponseMsg sendWithSync(RequestMsg message, int timeout);
}
