package com.github.knightliao.vpaas.lc.server.session.service.protocol;

import io.netty.channel.Channel;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/11 11:36
 */
public interface IProtocolProcessor {

    void doPre(Channel channel, Object msg);

    void doAfter(Channel channel, Object msg);

    void process(Channel channel, Object msg);

    boolean canGo(Object msg);
}
