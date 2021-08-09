package com.github.knightliao.vpaas.lc.server.connect.netty.handler;

import org.apache.commons.lang3.time.StopWatch;

import com.github.knightliao.vpaas.lc.server.connect.netty.statistics.service.LcCounterServiceFactory;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.channel.ChannelKeyUtils;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.LcHeartbeatMsg;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.DispatcherOpEnum;
import com.github.knightliao.vpaas.lc.server.connect.support.log.VpaasServerConnectLogUtils;
import com.github.knightliao.vpaas.lc.server.connect.support.utils.LcServiceTraceHandler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @date 2021/8/6 15:53
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerHeartbeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        try {

            //
            LcServiceTraceHandler.startTraceAndSession(ctx.channel());

            if (evt instanceof IdleStateHandler) {

                IdleStateEvent e = (IdleStateEvent) evt;

                if (e.state() == IdleState.WRITER_IDLE) {

                    // 写超时了
                    VpaasServerConnectLogUtils.doConnectLog(DispatcherOpEnum.writeTimeout, ctx.channel(), 0);
                    ctx.channel().close();
                    ChannelKeyUtils
                            .setChannelClientInactiveAttribute(ctx.channel(), DispatcherOpEnum.writeTimeout.getValue());

                } else if (e.state() == IdleState.READER_IDLE) {

                    // 读超时了
                    VpaasServerConnectLogUtils.doConnectLog(DispatcherOpEnum.readTimeout, ctx.channel(), 0);
                    ctx.channel().close();
                    ChannelKeyUtils
                            .setChannelClientInactiveAttribute(ctx.channel(), DispatcherOpEnum.readTimeout.getValue());

                } else if (e.state() == IdleState.ALL_IDLE) {

                    // 读超时了
                    VpaasServerConnectLogUtils.doConnectLog(DispatcherOpEnum.allTimeout, ctx.channel(), 0);
                    ctx.channel().close();
                    ChannelKeyUtils
                            .setChannelClientInactiveAttribute(ctx.channel(), DispatcherOpEnum.allTimeout.getValue());
                }
            }

            super.userEventTriggered(ctx, evt);

        } finally {

            //
            LcServiceTraceHandler.stopTrace();
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {

            //
            LcServiceTraceHandler.startTraceAndSession(ctx.channel());

            if (msg instanceof LcHeartbeatMsg) {

                //
                LcCounterServiceFactory.getILcCounterService().incrHeartbeatNum();

                return;
            }

            super.channelRead(ctx, msg);

        } finally {

            stopWatch.stop();
            VpaasServerConnectLogUtils.doConnectLog(DispatcherOpEnum.heartbeat, ctx.channel(), stopWatch.getTime());

            //
            LcServiceTraceHandler.stopTrace();
        }
    }
}
