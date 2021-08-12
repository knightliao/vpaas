package com.github.knightliao.vpaas.lc.server.server.service.impl.status;

import java.net.InetSocketAddress;

import org.apache.commons.lang3.StringUtils;

import com.github.knightliao.middle.log.LoggerUtil;
import com.github.knightliao.vpaas.common.utils.net.NettyUtils;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.param.LcServiceParam;
import com.github.knightliao.vpaas.lc.server.server.listener.status.StatusMessageListener;
import com.github.knightliao.vpaas.lc.server.server.service.impl.LcServerImpl;
import com.github.knightliao.vpaas.lc.server.server.service.impl.helper.ServerPipeline;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @date 2021/8/5 22:00
 */
@Slf4j
public class StatusLcServerImpl extends LcServerImpl {

    @Override
    protected void init() {

        // 配置
        LcServiceParam lcServiceParam = getLcServiceParam();
        lcServiceParam.setCheckHeartbeat(false);
        lcServiceParam.setOpenExecutor(false);
        lcServiceParam.setOpenCount(false);

        //
        super.init();

        //
        this.addChannelHandler("decoder", new StringDecoder());
        this.addChannelHandler("encoder", new StringEncoder());

        //
        this.addEventListener(new StatusMessageListener());

    }

    @Override
    public ChannelFuture bind() {

        init();

        //
        bootstrap = new ServerBootstrap();
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, getLcServiceParam().isKeepAlive());
        bootstrap.childOption(ChannelOption.TCP_NODELAY, getLcServiceParam().isTcpNoDelay());
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NettyUtils.useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);

        // child pipeline
        bootstrap.childHandler(ServerPipeline.getStatusChildChannelHandler(this, lcEventDispatcher));

        // 监听端口
        InetSocketAddress socketAddress = null;
        if (StringUtils.isBlank(getLcServiceParam().getIp())) {
            socketAddress = new InetSocketAddress(getLcServiceParam().getPort());
        } else {
            socketAddress = new InetSocketAddress(getLcServiceParam().getIp(), getLcServiceParam().getPort());
        }

        // bind
        ChannelFuture future = bootstrap.bind(socketAddress);

        // listener
        InetSocketAddress socketAddr = socketAddress;
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {

                channelFuture.await();
                if (channelFuture.isSuccess()) {
                    LoggerUtil.info(log, "Status Server started, listening on {0}, socketType: {1}", socketAddr,
                            lcServiceParam.getSocketType().getDesc());
                } else {
                    LoggerUtil.error(log, "Failed to start status server {0}, caused by {1}", socketAddr,
                            channelFuture.cause());
                }
            }
        });

        return future;
    }
}
