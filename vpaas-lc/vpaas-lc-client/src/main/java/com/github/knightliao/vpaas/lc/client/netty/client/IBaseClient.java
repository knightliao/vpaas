package com.github.knightliao.vpaas.lc.client.netty.client;

import java.net.SocketAddress;

import com.github.knightliao.vpaas.lc.client.support.dto.LcClientParam;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.RequestMsg;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.ResponseMsg;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * @author knightliao
 * @date 2021/8/8 22:43
 */
public interface IBaseClient {

    ChannelFuture connect(final SocketAddress socketAddress);

    ChannelFuture connect(final SocketAddress socketAddress, boolean sync);

    ChannelFuture send(Object msg);

    ResponseMsg sendWithSync(RequestMsg message);

    ResponseMsg sendWithSync(RequestMsg message, int timeout);

    void close();

    Channel getChannel();

    void setChannel(Channel channel);

    LcClientParam getLcClientParam();
}
