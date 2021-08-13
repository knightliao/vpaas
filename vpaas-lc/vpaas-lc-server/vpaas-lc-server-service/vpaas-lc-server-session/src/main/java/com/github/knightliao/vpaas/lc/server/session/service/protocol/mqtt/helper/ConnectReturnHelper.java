package com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.helper;

import org.springframework.stereotype.Service;

import com.github.knightliao.vpaas.lc.server.connect.support.enums.DispatcherOpEnum;
import com.github.knightliao.vpaas.lc.server.connect.support.log.VpaasServerConnectLogUtils;
import com.github.knightliao.vpaas.lc.server.session.service.support.enums.VpaasConnectCommonEnum;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/12 17:04
 */
@Service
public class ConnectReturnHelper {

    public MqttConnAckMessage doReturnFail(Channel channel, MqttConnectReturnCode mqttConnectReturnCode,
                                           VpaasConnectCommonEnum vpaasConnectCommonEnum) {

        MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttConnAckVariableHeader(mqttConnectReturnCode, false), null);
        channel.writeAndFlush(connAckMessage);

        //
        channel.close();

        // 关闭原因
        if (vpaasConnectCommonEnum.equals(VpaasConnectCommonEnum.CONNECT_AUTH_FAILED)) {
            VpaasServerConnectLogUtils.doConnectLog(DispatcherOpEnum.connectClose_AUTH_FAILED, channel, 0);

        } else if (vpaasConnectCommonEnum.equals(VpaasConnectCommonEnum.COMMON_CLIENT_ID_NULL)) {
            VpaasServerConnectLogUtils.doConnectLog(DispatcherOpEnum.connectClose_CLIENT_ID_NULL, channel, 0);

        } else if (vpaasConnectCommonEnum.equals(VpaasConnectCommonEnum.COMMON_USERNAME_ERROR)) {
            VpaasServerConnectLogUtils.doConnectLog(DispatcherOpEnum.connectClose_USERNAME_ERROR, channel, 0);

        } else {
            VpaasServerConnectLogUtils.doConnectLog(DispatcherOpEnum.connectClose_OTHERS, channel, 0);
        }

        return connAckMessage;
    }

    public MqttConnAckMessage doReturnAck(Channel channel) {

        MqttConnAckMessage connAckMessage = (MqttConnAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, true), null);
        channel.writeAndFlush(connAckMessage);
        return connAckMessage;
    }

}
