package com.github.knightliao.vpaas.lc.server.connect.netty.listener;

import java.util.EventListener;

import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.LcEventBehaviorEnum;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/7 17:54
 */
public interface LcExceptionEventListener extends EventListener {

    LcEventBehaviorEnum exceptionCaught(ChannelHandlerContext ctx, LcWrappedChannel channel, Throwable throwable);

}
