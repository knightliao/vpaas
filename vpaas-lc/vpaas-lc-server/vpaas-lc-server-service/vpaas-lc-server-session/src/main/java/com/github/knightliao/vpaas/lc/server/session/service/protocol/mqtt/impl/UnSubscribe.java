package com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.impl;

import org.springframework.stereotype.Service;

import com.github.knightliao.vpaas.lc.server.session.service.protocol.IProtocolProcessor;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/13 14:37
 */
@Service(value = "UnSubscribe")
public class UnSubscribe implements IProtocolProcessor {
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
        return msg instanceof MqttUnsubscribeMessage;
    }
}
