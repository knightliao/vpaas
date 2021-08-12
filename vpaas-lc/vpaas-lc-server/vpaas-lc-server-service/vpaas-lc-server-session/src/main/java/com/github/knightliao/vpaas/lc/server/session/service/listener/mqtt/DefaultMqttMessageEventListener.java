package com.github.knightliao.vpaas.lc.server.session.service.listener.mqtt;

import com.github.knightliao.middle.log.LoggerUtil;
import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.netty.listener.LcMessageEventListener;
import com.github.knightliao.vpaas.lc.server.connect.netty.statistics.service.LcCounterServiceFactory;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.channel.ChannelKeyUtils;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.LcEventBehaviorEnum;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckPayload;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @date 2021/8/10 16:10
 */
@Slf4j
public class DefaultMqttMessageEventListener implements LcMessageEventListener {

    @Override
    public LcEventBehaviorEnum channelRead(ChannelHandlerContext ctx, LcWrappedChannel channel, Object msg) {

        if (msg instanceof MqttMessage) {

            MqttMessage message = (MqttMessage) msg;

            MqttMessageType messageType = message.fixedHeader().messageType();

            switch (messageType) {

                case CONNECT:
                    this.connect(channel, (MqttConnectMessage) message);
                    break;

                case PUBLISH:
                    this.publish(channel, (MqttPublishMessage) message);
                    break;

                case SUBSCRIBE:
                    this.subscribe(channel, (MqttSubscribeMessage) message);
                    break;

                case UNSUBACK:
                    this.unSubscribe(channel, (MqttUnsubscribeMessage) message);
                    break;

                case PINGREQ:
                    this.pingReq(channel, message);
                    break;

                case PINGRESP:
                    this.pingResp(channel, message);
                    break;

                case DISCONNECT:
                    this.disConnect(channel, message);
                    break;

                default:
                    LoggerUtil.debug(log, "Nonsupport server message type of {0}", messageType);
                    break;
            }
        }

        return LcEventBehaviorEnum.CONTINUE;
    }

    protected void publish(LcWrappedChannel channel, MqttPublishMessage msg) {

    }

    protected void connect(LcWrappedChannel channel, MqttConnectMessage msg) {

        LoggerUtil.debug(log, "MQTT CONNECT received on channel {0} clientId is {1}",
                ChannelKeyUtils.getChannelLocalId(channel), msg.payload().clientIdentifier());

        MqttConnAckMessage okResp =
                (MqttConnAckMessage) MqttMessageFactory.newMessage(new MqttFixedHeader(
                                MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, true), null);
        channel.writeAndFlush(okResp);
    }

    protected void subscribe(LcWrappedChannel channel, MqttSubscribeMessage msg) {

        MqttSubscribeMessage subAckMessage = (MqttSubscribeMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()),
                new MqttSubAckPayload(0));
        channel.writeAndFlush(subAckMessage);
    }

    protected void unSubscribe(LcWrappedChannel channel, MqttUnsubscribeMessage msg) {

        MqttUnsubAckMessage unsubAckMessage = (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()), null);
        channel.writeAndFlush(unsubAckMessage);
    }

    protected void pingReq(LcWrappedChannel channel, MqttMessage msg) {

        LoggerUtil.debug(log, "MQTT pingReq received.");

        //
        LcCounterServiceFactory.getILcCounterService().incrHeartbeatNum();

        MqttMessage pingResp = new MqttMessage(new MqttFixedHeader(
                MqttMessageType.PINGRESP, false,
                MqttQoS.AT_MOST_ONCE, false, 0));
        channel.writeAndFlush(pingResp);
    }

    protected void pingResp(LcWrappedChannel channel, MqttMessage msg) {

        LoggerUtil.debug(log, "{0}", msg.toString());
    }

    protected void disConnect(LcWrappedChannel channel, MqttMessage msg) {

        if (channel.isActive()) {
            channel.close();
        }
    }
}
