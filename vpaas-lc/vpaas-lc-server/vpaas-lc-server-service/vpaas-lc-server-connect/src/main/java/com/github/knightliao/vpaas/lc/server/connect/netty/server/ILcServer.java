package com.github.knightliao.vpaas.lc.server.connect.netty.server;

import io.netty.channel.ChannelFuture;

/**
 * 服务核心接口
 *
 * @author knightliao
 * @date 2021/8/4 14:51
 */
public interface ILcServer {

    ChannelFuture bind();


}
