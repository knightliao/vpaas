package com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.impl;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Service;

import com.github.knightliao.vpaas.common.store.dto.SessionStoreDto;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.channel.ChannelKeyUtils;
import com.github.knightliao.vpaas.lc.server.session.service.dto.VpaasClientDto;
import com.github.knightliao.vpaas.lc.server.session.service.dto.VpaasCommonUserNameDto;
import com.github.knightliao.vpaas.lc.server.session.service.protocol.IProtocolProcessor;
import com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.helper.ConnectCheckHelper;
import com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.helper.ConnectHelper;
import com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.helper.ConnectLogHelper;
import com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.helper.ConnectReturnHelper;
import com.github.knightliao.vpaas.lc.server.session.service.support.enums.ClientUserLoginoutEnum;
import com.github.knightliao.vpaas.lc.server.session.service.support.enums.VpaasConnectCommonEnum;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/11 11:43
 */
@Service(value = "connect")
public class Connect implements IProtocolProcessor {

    @Resource
    private ConnectHelper connectHelper;

    @Resource
    private ConnectCheckHelper connectCheckHelper;

    @Resource
    private ConnectReturnHelper connectReturnHelper;

    @Resource
    private ConnectLogHelper connectLogHelper;

    @Override
    public void doPre(Channel channel, Object msg) {

    }

    @Override
    public void doAfter(Channel channel, Object msg) {

    }

    @Override
    public void process(Channel channel, Object msg) {

        String clientId = "";
        VpaasConnectCommonEnum vpaasConnectCommonEnum = null;
        VpaasClientDto vpaasClientDto = null;
        VpaasCommonUserNameDto vpaasCommonUserNameDto = null;
        MqttConnectMessage mqttConnectMessage = (MqttConnectMessage) msg;
        long preUid = ChannelKeyUtils.getChannelClientSessionUidAttribute(channel);
        SessionStoreDto preSession = null;

        try {

            // client id check
            clientId = mqttConnectMessage.payload().clientIdentifier();
            ImmutablePair<VpaasClientDto, VpaasConnectCommonEnum> clientCheck = checkClientId(mqttConnectMessage);
            vpaasConnectCommonEnum = clientCheck.getRight();
            vpaasClientDto = clientCheck.getLeft();
            if (vpaasConnectCommonEnum != null) {
                // 校验不通过
                return;
            }

            // username check
            ImmutablePair<VpaasCommonUserNameDto, VpaasConnectCommonEnum> userNameCheck =
                    checkUserName(channel, mqttConnectMessage);
            vpaasConnectCommonEnum = userNameCheck.getRight();
            vpaasCommonUserNameDto = userNameCheck.getLeft();
            if (vpaasConnectCommonEnum != null) {
                // 校验不通过
                return;
            }

            // check password (token is password)
            vpaasConnectCommonEnum =
                    connectCheckHelper.checkTokenValid(clientId, mqttConnectMessage.payload().password());
            if (vpaasConnectCommonEnum != null) {
                // 校验不通过
                return;
            }

            // quick login
            ImmutableTriple<SessionStoreDto, SessionStoreDto, VpaasConnectCommonEnum> quickCheckCheck =
                    connectHelper.quickSessionLogin(channel, vpaasClientDto, vpaasCommonUserNameDto,
                            mqttConnectMessage);
            if (quickCheckCheck != null) {
                // 原来已经有session，进行 return
                vpaasConnectCommonEnum = quickCheckCheck.getRight();
                preSession = quickCheckCheck.getMiddle();
                return;
            }

            // 完全是 新的会话
            connectHelper.saveToSession(channel, vpaasClientDto, vpaasCommonUserNameDto, mqttConnectMessage);
            connectHelper.setChannelAttribute(channel, clientId, vpaasCommonUserNameDto.getUid(),
                    vpaasCommonUserNameDto.getClientVer());
            vpaasConnectCommonEnum = VpaasConnectCommonEnum.CONNECT_NEW_OK;

        } catch (Exception ex) {

            vpaasConnectCommonEnum = VpaasConnectCommonEnum.CONNECT_UNKNOWN_ERROR;
            throw new RuntimeException(ex.toString(), ex);

        } finally {

            // 处理心跳包 这个一定要执行，不然会有频繁超时的问题
            connectHelper.replaceIdleHandler(channel, mqttConnectMessage);

            //
            doReturn(vpaasConnectCommonEnum, clientId, preUid, channel, vpaasCommonUserNameDto, preSession);

            //
            connectLogHelper.doConnectRealLog(vpaasConnectCommonEnum, clientId, vpaasCommonUserNameDto);
        }

    }

    private ImmutablePair<VpaasClientDto, VpaasConnectCommonEnum> checkClientId(MqttConnectMessage mqttConnectMessage) {

        VpaasClientDto vpaasClientDto = connectCheckHelper.checkClientId(mqttConnectMessage);
        VpaasConnectCommonEnum vpaasConnectCommonEnum = null;
        if (vpaasClientDto == null) {
            vpaasConnectCommonEnum = VpaasConnectCommonEnum.COMMON_CLIENT_ID_NULL;
        }

        return new ImmutablePair<>(vpaasClientDto, vpaasConnectCommonEnum);
    }

    private ImmutablePair<VpaasCommonUserNameDto, VpaasConnectCommonEnum> checkUserName(
            Channel channel, MqttConnectMessage mqttConnectMessage) {

        VpaasCommonUserNameDto userNameDto = connectCheckHelper.checkUserName(channel, mqttConnectMessage);
        VpaasConnectCommonEnum vpaasConnectCommonEnum = null;
        if (userNameDto == null) {
            vpaasConnectCommonEnum = VpaasConnectCommonEnum.COMMON_USERNAME_ERROR;
        }

        return new ImmutablePair<>(userNameDto, vpaasConnectCommonEnum);
    }

    @Override
    public boolean canGo(Object msg) {
        return true;
    }

    //
    private void doReturn(VpaasConnectCommonEnum vpaasConnectCommonEnum, String clientId, long preUid,
                          Channel channel, VpaasCommonUserNameDto vpaasCommonUserNameDto,
                          SessionStoreDto preSessionStoreDto) {

        ClientUserLoginoutEnum clientUserLoginoutEnum = null;
        if (vpaasCommonUserNameDto != null) {
            clientUserLoginoutEnum = vpaasCommonUserNameDto.getClientUserLoginout();
        }

        // auth
        if (vpaasConnectCommonEnum == VpaasConnectCommonEnum.CONNECT_AUTH_FAILED
                || vpaasConnectCommonEnum == VpaasConnectCommonEnum.COMMON_USERNAME_ERROR) {
            connectReturnHelper.doReturnFail(channel,
                    MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, vpaasConnectCommonEnum);

            // client id
        } else if (vpaasConnectCommonEnum == VpaasConnectCommonEnum.COMMON_CLIENT_ID_NULL) {
            connectReturnHelper.doReturnFail(channel,
                    MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, vpaasConnectCommonEnum);

            // token
        } else if (vpaasConnectCommonEnum == VpaasConnectCommonEnum.CONNECT_TOKEN_EXPIRE
                || vpaasConnectCommonEnum == VpaasConnectCommonEnum.CONNECT_TOKEN_NO_RIGHT) {
            connectReturnHelper.doReturnFail(channel,
                    MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED, vpaasConnectCommonEnum);

        } else if (vpaasConnectCommonEnum == VpaasConnectCommonEnum.CONNECT_QUICK_LOCAL_REPLACE
                || vpaasConnectCommonEnum == VpaasConnectCommonEnum.CONNECT_QUICK_LOCAL_REPLACE_SELF
                || vpaasConnectCommonEnum == VpaasConnectCommonEnum.CONNECT_QUICK_REMOTE_REPLACE
                || vpaasConnectCommonEnum == VpaasConnectCommonEnum.CONNECT_NEW_OK) {
            //
            connectReturnHelper.doReturnAck(channel);

        } else {

            // 其它情况
            connectReturnHelper.doReturnFail(channel,
                    MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD,
                    vpaasConnectCommonEnum);
        }

    }
}
