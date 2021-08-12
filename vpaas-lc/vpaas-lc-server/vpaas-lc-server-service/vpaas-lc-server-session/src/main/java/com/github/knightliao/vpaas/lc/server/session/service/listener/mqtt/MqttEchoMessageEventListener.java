package com.github.knightliao.vpaas.lc.server.session.service.listener.mqtt;

import org.apache.commons.lang3.RandomUtils;

import com.github.knightliao.middle.log.LoggerUtil;
import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @date 2021/8/10 16:09
 */
@Slf4j
public class MqttEchoMessageEventListener extends DefaultMqttMessageEventListener {

    @Override
    public void publish(LcWrappedChannel channel, MqttPublishMessage msg) {

        String topic = msg.variableHeader().topicName();
        ByteBuf buf = msg.content().duplicate();
        byte[] tmp = new byte[buf.readableBytes()];
        buf.readBytes(tmp);
        String content = new String(tmp);

        content = String.format("%d, %s", RandomUtils.nextInt(), content);

        LoggerUtil.info(log, "{0}", content);

        MqttPublishMessage sendMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttPublishVariableHeader(topic, 0),
                Unpooled.buffer().writeBytes(content.getBytes()));
        channel.writeAndFlush(sendMessage);
    }
}
