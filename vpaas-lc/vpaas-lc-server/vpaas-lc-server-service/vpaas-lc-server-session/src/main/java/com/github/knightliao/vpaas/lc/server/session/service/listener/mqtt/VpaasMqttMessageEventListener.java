package com.github.knightliao.vpaas.lc.server.session.service.listener.mqtt;

import org.springframework.stereotype.Service;

import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.netty.listener.LcChannelEventListener;
import com.github.knightliao.vpaas.lc.server.connect.netty.listener.LcExceptionEventListener;
import com.github.knightliao.vpaas.lc.server.connect.netty.listener.LcMessageEventListener;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.LcEventBehaviorEnum;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author knightliao
 * @date 2021/8/10 16:09
 */
@Service
public class VpaasMqttMessageEventListener implements LcMessageEventListener, LcChannelEventListener,
        LcExceptionEventListener {

    @Override
    public LcEventBehaviorEnum channelActive(ChannelHandlerContext ctx, LcWrappedChannel channel) {
        return null;
    }

    @Override
    public LcEventBehaviorEnum channelInactive(ChannelHandlerContext ttx, LcWrappedChannel channel) {
        return null;
    }

    @Override
    public LcEventBehaviorEnum exceptionCaught(ChannelHandlerContext ctx, LcWrappedChannel channel,
                                               Throwable throwable) {
        return null;
    }

    @Override
    public LcEventBehaviorEnum channelRead(ChannelHandlerContext ctx, LcWrappedChannel channel, Object msg) {
        return null;
    }
}
