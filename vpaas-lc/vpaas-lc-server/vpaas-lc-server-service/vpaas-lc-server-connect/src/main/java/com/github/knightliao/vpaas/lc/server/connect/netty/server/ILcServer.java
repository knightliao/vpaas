package com.github.knightliao.vpaas.lc.server.connect.netty.server;

import java.util.EventListener;
import java.util.Map;

import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;

/**
 * 服务核心接口
 *
 * @author knightliao
 * @email knightliao@gmail.com
* @email knightliao@gmail.com
 * @date 2021/8/4 14:51
 */
public interface ILcServer {

    ChannelFuture bind();

    LcWrappedChannel getChannel(String channelId);

    LcWrappedChannel getChannelByGroup(ChannelId channelId);

    Map<String, LcWrappedChannel> getChannels();

    long incrChannelMaxSize(boolean incr);

    ChannelGroup getChannelGroup();

    // 添加自定义 事件处理器
    void addEventListener(EventListener listener);

    // 添加自定义 channel handler
    void addChannelHandler(String key, ChannelHandler channelHandler);

}


