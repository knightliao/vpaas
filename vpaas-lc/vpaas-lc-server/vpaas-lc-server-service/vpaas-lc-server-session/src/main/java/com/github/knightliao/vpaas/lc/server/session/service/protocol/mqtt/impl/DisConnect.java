package com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.impl;

import org.springframework.stereotype.Service;

import com.github.knightliao.vpaas.lc.server.connect.support.dto.channel.ChannelKeyUtils;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.DispatcherOpEnum;
import com.github.knightliao.vpaas.lc.server.connect.support.log.VpaasServerConnectLogUtils;
import com.github.knightliao.vpaas.lc.server.session.service.protocol.IProtocolProcessor;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/13 14:36
 */
@Service(value = "DisConnect")
public class DisConnect implements IProtocolProcessor {
    @Override
    public void doPre(Channel channel, Object msg) {

    }

    @Override
    public void doAfter(Channel channel, Object msg) {

    }

    @Override
    public void process(Channel channel, Object msg) {

        //
        VpaasServerConnectLogUtils.doConnectLog(DispatcherOpEnum.disconnect, channel, 0);
        ChannelKeyUtils.setChannelClientInactiveAttribute(channel, DispatcherOpEnum.disconnect.getValue());

        String clientId = ChannelKeyUtils.getChannelClientSessionAttribute(channel);

        MqttMessage mqttMessage = (MqttMessage) msg;

        //
        channel.close();
    }

    @Override
    public boolean canGo(Object msg) {
        return msg instanceof MqttMessage;
    }
}
