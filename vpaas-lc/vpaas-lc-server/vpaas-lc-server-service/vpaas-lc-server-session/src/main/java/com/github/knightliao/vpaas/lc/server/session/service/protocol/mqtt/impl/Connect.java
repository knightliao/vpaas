package com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.knightliao.vpaas.lc.server.session.service.dto.VpaasClientDto;
import com.github.knightliao.vpaas.lc.server.session.service.dto.VpaasCommonUserNameDto;
import com.github.knightliao.vpaas.lc.server.session.service.protocol.ProtocolProcessor;
import com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.helper.ConnectHelper;
import com.github.knightliao.vpaas.lc.server.session.service.support.enums.VpaasConnectCommonEnum;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnectMessage;

/**
 * @author knightliao
 * @date 2021/8/11 11:43
 */
@Service(value = "connect")
public class Connect implements ProtocolProcessor {

    @Resource
    private ConnectHelper connectHelper;

    @Override
    public void doPre(Channel channel, Object msg) {

    }

    @Override
    public void doAfter(Channel channel, Object msg) {

    }

    @Override
    public void process(Channel channel, Object msg) {

        String clientId = "";
        String userName = "";
        VpaasConnectCommonEnum vpaasConnectCommonEnum = null;

        MqttConnectMessage mqttConnectMessage = (MqttConnectMessage) msg;

        try {

            // client id
            clientId = mqttConnectMessage.payload().clientIdentifier();
            VpaasClientDto vpaasClientDto = connectHelper.checkClientId(mqttConnectMessage);
            if (vpaasClientDto == null) {
                vpaasConnectCommonEnum = VpaasConnectCommonEnum.COMMON_CLIENT_ID_NULL;
                return ;
            }

            // 校验 username 并获取step
            VpaasCommonUserNameDto userNameDto =  connectHelper.checkUserName(mqttConnectMessage);
            if (userNameDto == null) {
                vpaasConnectCommonEnum = VpaasConnectCommonEnum.COMMON_USERNAME_ERROR;
                return ;
            }

            // password


        } finally {

        }

    }

    @Override
    public boolean canGo(Object msg) {
        return false;
    }
}
