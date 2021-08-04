package com.github.knightliao.vpaas.lc.server.connect.dispatch.dispatcher.impl;

import com.github.knightliao.vpaas.lc.server.connect.dispatch.dispatcher.ILcEventDispatcher;
import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.netty.service.LcService;

import io.netty.channel.ChannelHandlerContext;

/**
 * 事件分发器
 *
 * @author knightliao
 * @date 2021/8/4 23:15
 */
public class LcEventDispatcherImpl implements ILcEventDispatcher {

    private LcService lcService;

    public LcEventDispatcherImpl(LcService lcService) {
        if (lcService == null) {
            throw new IllegalArgumentException("service is null");
        }

        this.lcService = lcService;
    }

    @Override
    public void dispatchMessageEvent(ChannelHandlerContext channelHandlerContext, LcWrappedChannel channel,
                                     Object msg) {

    }

    @Override
    public void dispatchChannelEvent(ChannelHandlerContext channelHandlerContext,
                                     LcWrappedChannel channel) {

    }

    @Override
    public void dispatchExceptionEvent(ChannelHandlerContext channelHandlerContext,
                                       LcWrappedChannel channel, Throwable cause) {

    }
}
