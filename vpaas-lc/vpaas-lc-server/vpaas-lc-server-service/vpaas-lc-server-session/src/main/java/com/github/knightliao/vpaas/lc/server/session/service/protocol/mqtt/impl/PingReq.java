package com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.impl;

import org.springframework.stereotype.Service;

import com.github.knightliao.vpaas.lc.server.session.service.protocol.ProtocolProcessor;

import io.netty.channel.Channel;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/13 14:35
 */
@Service(value = "PingReq")
public class PingReq  implements ProtocolProcessor {
    @Override
    public void doPre(Channel channel, Object msg) {

    }

    @Override
    public void doAfter(Channel channel, Object msg) {

    }

    @Override
    public void process(Channel channel, Object msg) {

    }

    @Override
    public boolean canGo(Object msg) {
        return false;
    }
}
