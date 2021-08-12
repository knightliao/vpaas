package com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.helper;

import org.springframework.stereotype.Service;

import com.github.knightliao.vpaas.lc.server.session.service.dto.VpaasClientDto;
import com.github.knightliao.vpaas.lc.server.session.service.dto.VpaasCommonUserNameDto;

import io.netty.handler.codec.mqtt.MqttConnectMessage;

/**
 * @author knightliao
 * @date 2021/8/12 17:04
 */
@Service
public class ConnectHelper {

    // 确定client id是否Ok
    public VpaasClientDto checkClientId(MqttConnectMessage mqttConnectMessage) {

        // 获取client id
        // clientid 为空或 null的情况，这里要求客户端必须提供clientId, 不管cleansession是否为1，此处没有参考标准协议实现
        return VpaasClientDto.parse(mqttConnectMessage.payload().clientIdentifier());
    }

    // 确定 username 是否Ok
    public VpaasCommonUserNameDto checkUserName(MqttConnectMessage mqttConnectMessage) {

        return VpaasCommonUserNameDto.parse(mqttConnectMessage.payload().userName());
    }
}
