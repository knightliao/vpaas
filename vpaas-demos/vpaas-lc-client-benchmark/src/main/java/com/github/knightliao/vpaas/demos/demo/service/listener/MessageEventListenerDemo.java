package com.github.knightliao.vpaas.demos.demo.service.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.knightliao.middle.log.LoggerUtil;
import com.github.knightliao.middle.trace.MyTraceUtils;
import com.github.knightliao.vpaas.demos.demo.service.ClientSimulationService;
import com.github.knightliao.vpaas.demos.demo.support.constants.VpaasClientDemoConstants;
import com.github.knightliao.vpaas.demos.demo.support.dto.ClientDemoContext;
import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.netty.listener.LcChannelEventListener;
import com.github.knightliao.vpaas.lc.server.connect.netty.listener.LcMessageEventListener;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.LcEventBehaviorEnum;
import com.github.knightliao.vpaas.lc.server.connect.support.utils.LcServiceTraceHelper;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/29 13:35
 */
public class MessageEventListenerDemo implements LcMessageEventListener, LcChannelEventListener {

    protected static final Logger LISTENER_LOG = LoggerFactory.getLogger(VpaasClientDemoConstants.LISTENER_LOG);

    private ClientSimulationService clientSimulationService;
    private ClientDemoContext clientDemoContext;

    public MessageEventListenerDemo(ClientSimulationService clientSimulationService,
                                    ClientDemoContext clientDemoContext) {
        this.clientDemoContext = clientDemoContext;
        this.clientSimulationService = clientSimulationService;
    }

    @Override
    public LcEventBehaviorEnum channelActive(ChannelHandlerContext ctx, LcWrappedChannel channel) {
        return LcEventBehaviorEnum.CONTINUE;
    }

    @Override
    public LcEventBehaviorEnum channelInactive(ChannelHandlerContext ttx, LcWrappedChannel channel) {

        clientSimulationService.setConnectStatus("channelInactive", false, false);
        return LcEventBehaviorEnum.CONTINUE;
    }

    @Override
    public LcEventBehaviorEnum channelRead(ChannelHandlerContext ctx, LcWrappedChannel channel, Object msg) {

        try {

            //
            LcServiceTraceHelper.startTraceAndSession(channel);

            MyTraceUtils.newReqId();

            if (msg instanceof MqttMessage) {

                MqttMessage mqttMessage = (MqttMessage) msg;
                MqttMessageType mqttMessageType = mqttMessage.fixedHeader().messageType();

                switch (mqttMessageType) {

                    case CONNECT:
                        break;

                    case CONNACK:
                        LoggerUtil.info(LISTENER_LOG, " {0}", msg.toString());
                        processConAck(channel, (MqttConnAckMessage) mqttMessage);
                        break;

                    case PUBLISH:
                        processPublish(mqttMessage);
                        break;

                    case SUBSCRIBE:
                    case UNSUBACK:
                    case PINGREQ:
                        break;

                    case PINGRESP:
                        LoggerUtil.info(LISTENER_LOG, "PINGRESP");
                        clientSimulationService.setConnectStatus("PINGRESP", true, true);
                        break;

                    case DISCONNECT:
                        LoggerUtil.info(LISTENER_LOG, "DISCONNECT {0}", msg.toString());
                        clientSimulationService.setConnectStatus("DISCONNECT", false, false);
                        break;

                    default:
                        LoggerUtil.error(LISTENER_LOG, "Notsupported {0}", mqttMessageType);
                        break;
                }
            }

            return LcEventBehaviorEnum.CONTINUE;

        } catch (Throwable ex) {

            throw new RuntimeException(ex);

        } finally {
            LcServiceTraceHelper.stopTrace();
        }
    }

    private void processConAck(Channel channel, MqttConnAckMessage mqttConnAckMessage) {

        if (mqttConnAckMessage.variableHeader().connectReturnCode().equals(MqttConnectReturnCode.CONNECTION_ACCEPTED)) {
            clientSimulationService.setConnectStatus("CONNECTION_ACCEPTED", true, true);

        } else if (mqttConnAckMessage.variableHeader().connectReturnCode()
                .equals(MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED)) {
            clientSimulationService.setConnectStatus("CONNECTION_REFUSED_NOT_AUTHORIZED", true, false);
            // token过期，需要重新获取
            clientSimulationService.setToken("");

        } else {

            clientSimulationService.setConnectStatus("CONNECTION_REFUSED", true, false);
            // token过期，需要重新获取
            clientSimulationService.setToken("");
        }
    }

    private void processPublish(MqttMessage mqttMessage) {

        MqttPublishMessage mqttPublishMessage = (MqttPublishMessage) mqttMessage;

        // do ack
        clientSimulationService.doPubAck(mqttPublishMessage.variableHeader().packetId());
    }
}
