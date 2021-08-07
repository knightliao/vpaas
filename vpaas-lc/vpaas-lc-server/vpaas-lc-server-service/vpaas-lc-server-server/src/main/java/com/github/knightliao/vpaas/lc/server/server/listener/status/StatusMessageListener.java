package com.github.knightliao.vpaas.lc.server.server.listener.status;

import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.netty.listener.LcMessageEventListener;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.LcEventBehaviorEnum;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author knightliao
 * @date 2021/8/7 18:25
 */
public class StatusMessageListener implements LcMessageEventListener {

    @Override
    public LcEventBehaviorEnum channelRead(ChannelHandlerContext ctx, LcWrappedChannel channel, Object msg) {
        return null;
    }
}
