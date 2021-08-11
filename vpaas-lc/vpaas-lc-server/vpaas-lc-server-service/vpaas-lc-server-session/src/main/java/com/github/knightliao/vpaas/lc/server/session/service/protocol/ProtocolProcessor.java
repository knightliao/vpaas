package com.github.knightliao.vpaas.lc.server.session.service.protocol;

import io.netty.channel.Channel;

/**
 * @author knightliao
 * @date 2021/8/11 11:36
 */
public interface ProtocolProcessor {

    void doPre(Channel channel, Object msg);

    void doAfter(Channel channel, Object msg);

    void process(Channel channel, Object msg);

    boolean canGo(Object msg);
}
