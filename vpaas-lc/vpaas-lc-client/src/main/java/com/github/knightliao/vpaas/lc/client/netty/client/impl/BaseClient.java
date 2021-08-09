package com.github.knightliao.vpaas.lc.client.netty.client.impl;

import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.github.knightliao.vpaas.common.utils.exceptions.SocketRuntimeException;
import com.github.knightliao.vpaas.common.utils.log.LoggerUtil;
import com.github.knightliao.vpaas.lc.client.netty.channel.LcWrappedClientChannel;
import com.github.knightliao.vpaas.lc.client.netty.client.IBaseClient;
import com.github.knightliao.vpaas.lc.client.netty.pipeline.LcClientPipeline;
import com.github.knightliao.vpaas.lc.client.support.dto.LcClientParam;
import com.github.knightliao.vpaas.lc.server.connect.dispatch.dispatcher.LcEventDispatcherFactory;
import com.github.knightliao.vpaas.lc.server.connect.netty.service.LcService;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.RequestMsg;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.ResponseMsg;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @date 2021/8/8 17:59
 */
@Slf4j
public class BaseClient extends LcService implements IBaseClient {

    // 当前连接server
    protected SocketAddress curServer;
    protected Channel channel;

    // 信号量 来支持并发connect
    protected Semaphore semaphore = new Semaphore(0);

    // group 只能有一个
    protected static EventLoopGroup group = new NioEventLoopGroup();
    protected Bootstrap bootstrap;

    //
    protected LcClientParam lcClientParam = new LcClientParam();

    @Override
    public void init() {

        super.init();

        //
        lcEventDispatcher = LcEventDispatcherFactory.getEventDispatcherDefaultImpl(this);

        //
        bootstrap = new Bootstrap();
    }

    private ChannelFuture doConnect(final SocketAddress socketAddress, boolean sync) {

        // 连接server
        curServer = socketAddress;

        //
        try {

            ChannelFuture channelFuture = bootstrap.connect(socketAddress).sync();

            channelFuture.addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {

                    channelFuture.await();

                    if (channelFuture.isSuccess()) {
                        channel = new LcWrappedClientChannel(channelFuture.channel());

                        //
                        LoggerUtil.debug(log, "Connect to {0} success", socketAddress);
                    } else {
                        LoggerUtil.error(log, "Failed to connect to {0}, caused by: {1}", socketAddress,
                                channelFuture.cause());
                    }

                    // 释放所占用的信号量
                    semaphore.release(Integer.MAX_VALUE - semaphore.availablePermits());
                }
            });

            channelFuture.channel().closeFuture();

            // 是否同步
            if (sync) {

                Throwable cause = null;
                try {
                    if (!semaphore.tryAcquire(lcClientParam.getConnectTimeout(), TimeUnit.MILLISECONDS)) {
                        cause = new SocketTimeoutException("time out");
                    }
                } catch (InterruptedException ex) {
                    throw new SocketRuntimeException(ex);
                }

                if (cause != null) {
                    throw new SocketRuntimeException(cause);
                }
            }

            return channelFuture;

        } catch (Exception ex) {

            throw new SocketRuntimeException(ex);
        }
    }

    @Override
    public void shutdown() {

        if (group != null) {
            group.shutdownGracefully();
        }
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress) {
        return connect(socketAddress, true);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketAddress, boolean sync) {

        init();

        bootstrap.option(ChannelOption.SO_KEEPALIVE, getLcServiceParam().isKeepAlive());
        bootstrap.option(ChannelOption.TCP_NODELAY, getLcServiceParam().isTcpNoDelay());
        bootstrap.group(group).channel(NioSocketChannel.class);

        bootstrap.handler(LcClientPipeline.getChildChannelHandler(this, lcEventDispatcher, lcClientParam));

        return doConnect(socketAddress, sync);
    }

    @Override
    public ChannelFuture send(Object msg) {

        if (channel == null) {
            throw new SocketRuntimeException("channel have not connect.");
        }
        return ((LcWrappedClientChannel) channel).send(msg);
    }

    @Override
    public ResponseMsg sendWithSync(RequestMsg message) {

        if (channel == null) {
            throw new SocketRuntimeException("channel have not connect.");
        }
        return ((LcWrappedClientChannel) channel).sendSync(message, lcClientParam.getSyncInvokeTimeout());
    }

    @Override
    public ResponseMsg sendWithSync(RequestMsg message, int timeout) {

        if (channel == null) {
            throw new SocketRuntimeException("channel have not connect.");
        }
        return ((LcWrappedClientChannel) channel).sendSync(message, timeout);
    }

    @Override
    public void close() {

        if (channel != null) {
            channel.close();
            channel = null;
        }
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public LcClientParam getLcClientParam() {
        return lcClientParam;
    }
}
