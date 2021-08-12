package com.github.knightliao.vpaas.lc.client.netty.handler;

import java.io.IOException;

import org.apache.commons.lang3.time.StopWatch;

import com.github.knightliao.vpaas.lc.client.netty.client.IBaseClient;
import com.github.knightliao.vpaas.lc.server.connect.dispatch.dispatcher.ILcEventDispatcher;
import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.netty.service.ILcService;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.DispatcherOpEnum;
import com.github.knightliao.vpaas.lc.server.connect.support.log.VpaasServerConnectLogUtils;
import com.github.knightliao.vpaas.lc.server.connect.support.utils.LcServiceTraceHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/8 18:03
 */
@Slf4j
public class ClientDispatchHandler extends ChannelInboundHandlerAdapter {

    private ILcEventDispatcher eventDispatcher;

    protected ILcService lcService;

    public ClientDispatchHandler(ILcEventDispatcher eventDispatcher, ILcService lcService) {

        if (eventDispatcher == null) {
            throw new IllegalArgumentException("eventDispatcher");
        }

        this.eventDispatcher = eventDispatcher;
        this.lcService = lcService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {

            //
            LcServiceTraceHandler.startTraceAndSession(ctx.channel());

            LcWrappedChannel channel = (LcWrappedChannel) ((IBaseClient) lcService).getChannel();
            eventDispatcher.dispatchMessageEvent(ctx, channel, msg);

        } finally {

            stopWatch.stop();
            VpaasServerConnectLogUtils
                    .doConnectLog(DispatcherOpEnum.channelRead, ctx.channel(), stopWatch.getTime());

            //
            LcServiceTraceHandler.stopTrace();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {

            //
            LcServiceTraceHandler.startTraceAndSession(ctx.channel());

            LcWrappedChannel channel = (LcWrappedChannel) ((IBaseClient) lcService).getChannel();
            if (channel == null) {
                return;
            }
            eventDispatcher.dispatchChannelEvent(ctx, channel);

        } finally {

            stopWatch.stop();
            VpaasServerConnectLogUtils
                    .doConnectLog(DispatcherOpEnum.channelActive, ctx.channel(), stopWatch.getTime());

            //
            LcServiceTraceHandler.stopTrace();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {

            closeChannel(ctx);

        } finally {

            stopWatch.stop();
            VpaasServerConnectLogUtils
                    .doConnectLog(DispatcherOpEnum.channelInActive, ctx.channel(), stopWatch.getTime());

            //
            LcServiceTraceHandler.stopTrace();
            LcServiceTraceHandler.clearChannelSession();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {

            //
            LcServiceTraceHandler.startTraceAndSession(ctx.channel());

            LcWrappedChannel channel = (LcWrappedChannel) ((IBaseClient) lcService).getChannel();
            if (channel == null) {
                return;
            }
            eventDispatcher.dispatchExceptionEvent(ctx, channel, cause);

            // 处理IOException
            if (cause instanceof IOException) {
                ctx.close();
                closeChannel(ctx);
            }

        } finally {

            stopWatch.stop();
            VpaasServerConnectLogUtils
                    .doConnectLog(DispatcherOpEnum.exceptionCaught, ctx.channel(), stopWatch.getTime());

            //
            LcServiceTraceHandler.stopTrace();
            LcServiceTraceHandler.clearChannelSession();
        }
    }

    private void closeChannel(ChannelHandlerContext ctx) {

        LcWrappedChannel channel = (LcWrappedChannel) ((IBaseClient) lcService).getChannel();

        if (channel != null) {
            ((IBaseClient) lcService).setChannel(null);

            eventDispatcher.dispatchChannelEvent(ctx, channel);
        }
    }
}
