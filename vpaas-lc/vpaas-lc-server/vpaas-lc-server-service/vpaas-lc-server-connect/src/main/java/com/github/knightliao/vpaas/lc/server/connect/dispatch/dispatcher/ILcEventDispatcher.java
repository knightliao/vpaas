package com.github.knightliao.vpaas.lc.server.connect.dispatch.dispatcher;

import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author knightliao
 * @date 2021/8/4 23:12
 */
public interface ILcEventDispatcher {

    void dispatchMessageEvent(final ChannelHandlerContext channelHandlerContext, final LcWrappedChannel channel,
                              final Object msg);

    void dispatchChannelEvent(final ChannelHandlerContext channelHandlerContext, final LcWrappedChannel channel);

    void dispatchExceptionEvent(final ChannelHandlerContext channelHandlerContext, final LcWrappedChannel channel,
                                final Throwable cause);

}
