package com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.impl;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.github.knightliao.vpaas.common.rely.message.IMessageIdService;
import com.github.knightliao.vpaas.common.store.ISessionStoreService;
import com.github.knightliao.vpaas.common.store.dto.SessionStoreDto;
import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.netty.server.LcServerContext;
import com.github.knightliao.vpaas.lc.server.session.service.protocol.IProtocolProcessor;
import com.github.knightliao.vpaas.lc.server.session.service.support.utils.TopicKeyUtils;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/13 14:36
 */
@Service(value = "publish")
@Slf4j
public class Publish implements IProtocolProcessor {

    @Resource
    private IMessageIdService iMessageIdService;

    @Resource
    private ISessionStoreService sessionStoreService;

    @Override
    public void doPre(Channel channel, Object msg) {

    }

    @Override
    public void doAfter(Channel channel, Object msg) {

    }

    // 这里无论发送成功还是失败，都需要回复ack
    // 如何保证成功？依赖 异步队列来保证
    @Override
    public void process(Channel channel, Object msg) {

        // msg
        MqttPublishMessage mqttPublishMessage = (MqttPublishMessage) msg;

        // get topic
        String topicName = mqttPublishMessage.variableHeader().topicName();

        //
        String clientId = TopicKeyUtils.getClientIdFromP2pTopic(topicName);
        if (!StringUtils.isEmpty(clientId)) {
            // send to target client id
            byte[] messageBytes = new byte[mqttPublishMessage.payload().readableBytes()];
            sendPublishMessage(topicName, clientId, messageBytes);
        }

        // ack
        sendPubAckMessage(channel, mqttPublishMessage.variableHeader().packetId());
    }

    @Override
    public boolean canGo(Object msg) {
        return msg instanceof MqttPublishMessage;
    }

    private boolean sendPublishMessage(String topic, String clientId, byte[] messageBytes) {

        // prepare msg
        int messageId = iMessageIdService.getNextMessageId(clientId);
        MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttPublishVariableHeader(topic, messageId), Unpooled.buffer().writeBytes(messageBytes));

        // get session
        SessionStoreDto sessionStoreDto = sessionStoreService.get(LcServerContext.getContext().getBrokerId(), clientId);
        if (sessionStoreDto == null) {
            return false;
        }

        // get target channel ,in current map
        LcWrappedChannel targetChannel =
                LcServerContext.getContext().getServer().getChannel(sessionStoreDto.getChannelLocalId());
        if (targetChannel == null) {
            return false;
        }

        // do send
        targetChannel.writeAndFlush(publishMessage);

        return true;
    }

    private void sendPubAckMessage(Channel channel, int messageId) {

        MqttPubAckMessage pubAckMessage = (MqttPubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId), null);
        channel.writeAndFlush(pubAckMessage);
    }
}
