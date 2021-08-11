package com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.knightliao.vpaas.lc.server.session.service.protocol.ProtocolProcessor;

import io.netty.channel.Channel;

/**
 * @author knightliao
 * @date 2021/8/11 11:43
 */
@Service(value = "connect")
public class Connect implements ProtocolProcessor {


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
