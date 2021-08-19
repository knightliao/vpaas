package com.github.knightliao.vpaas.lc.server.connect.netty.handler;

import java.io.IOException;

import org.apache.commons.lang3.time.StopWatch;

import com.github.knightliao.middle.log.LoggerUtil;
import com.github.knightliao.middle.utils.log.LogControllerTemplate;
import com.github.knightliao.vpaas.lc.server.connect.dispatch.dispatcher.ILcEventDispatcher;
import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.netty.server.ILcServer;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.channel.ChannelKeyUtils;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.DispatcherOpEnum;
import com.github.knightliao.vpaas.lc.server.connect.support.log.VpaasServerConnectLogUtils;
import com.github.knightliao.vpaas.lc.server.connect.support.utils.LcServiceTraceHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/7 10:40
 */
@Slf4j
public class ServerDispatchHandler extends ChannelInboundHandlerAdapter {

    protected ILcEventDispatcher eventDispatcher;
    protected ILcServer lcServer;

    public ServerDispatchHandler(ILcEventDispatcher eventDispatcher, ILcServer lcServer) {
        if (eventDispatcher == null) {
            throw new IllegalArgumentException("eventDispatcher cannot be null");
        }

        this.eventDispatcher = eventDispatcher;
        this.lcServer = lcServer;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {

            //
            LcServiceTraceHandler.startTraceAndSession(ctx.channel());

            //
            LcWrappedChannel channel = lcServer.getChannelByGroup(ctx.channel().id());

            // retain data
            // 因为是异步去做，因此需要做 retain 保留
            if (msg instanceof ByteBuf) {
                ((ByteBuf) msg).retain();
            }

            // dispatcher
            eventDispatcher.dispatchMessageEvent(ctx, channel, msg);

            super.channelRead(ctx, msg);

        } finally {

            //
            stopWatch.stop();
            VpaasServerConnectLogUtils.doConnectLog(DispatcherOpEnum.channelRead, ctx.channel(), stopWatch.getTime());

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

            //
            LcWrappedChannel channel = new LcWrappedChannel(ctx.channel());
            String channelLocalId = ChannelKeyUtils.getChannelLocalId(channel);

            // channel 添加进来
            lcServer.getChannels().put(channelLocalId, channel);
            lcServer.getChannelGroup().add(channel);

            // dispatcher
            eventDispatcher.dispatchChannelEvent(ctx, channel);

        } finally {

            //
            stopWatch.stop();
            VpaasServerConnectLogUtils.doConnectLog(DispatcherOpEnum.channelActive, ctx.channel(), stopWatch.getTime());

            //
            LcServiceTraceHandler.stopTrace();
        }

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {

            closeChannel(ctx);

        } finally {

            //
            stopWatch.stop();
            VpaasServerConnectLogUtils
                    .doConnectLog(DispatcherOpEnum.channelInActive, ctx.channel(), stopWatch.getTime());

            //
            LcServiceTraceHandler.clearChannelSession();
            LcServiceTraceHandler.stopTrace();
        }

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {

            //
            LcServiceTraceHandler.startTraceAndSession(ctx.channel());

            //
            LcWrappedChannel channel = lcServer.getChannelByGroup(ctx.channel().id());
            if (channel != null) {
                eventDispatcher.dispatchExceptionEvent(ctx, channel, cause);
            }

            // 处理ioexception , 主动关闭channel
            if (cause instanceof IOException) {
                ctx.close();
                closeChannel(ctx);
            }

        } finally {

            //
            stopWatch.stop();

            //
            if (cause instanceof IOException) {
                VpaasServerConnectLogUtils
                        .doConnectLog(DispatcherOpEnum.exceptionCaughtIo, ctx.channel(), stopWatch.getTime());
            } else {

                // 日志可能太多
                LogControllerTemplate.doFewLog(100, () -> {
                    LoggerUtil.error(cause, log, "{0}", cause.toString());
                });
            }

            //
            LcServiceTraceHandler.clearChannelSession();
            LcServiceTraceHandler.stopTrace();
        }

        super.exceptionCaught(ctx, cause);
    }

    private void closeChannel(ChannelHandlerContext ctx) {

        String channelLocalId = ChannelKeyUtils.getChannelLocalId(ctx.channel());

        // 删除本地的channel map
        LcWrappedChannel channel = lcServer.getChannels().remove(channelLocalId);
        lcServer.getChannelGroup().remove(ctx.channel());

        if (channel != null) {
            eventDispatcher.dispatchChannelEvent(ctx, channel);
        }

        ctx.close();
    }
}
