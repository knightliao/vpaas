package com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.helper;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.github.knightliao.middle.lang.constants.PackConstants;
import com.github.knightliao.vpaas.common.store.ISessionStoreService;
import com.github.knightliao.vpaas.common.store.ITokenStoreService;
import com.github.knightliao.vpaas.common.store.dto.SessionStoreDto;
import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.netty.server.LcServerContext;
import com.github.knightliao.vpaas.lc.server.connect.netty.service.ILcService;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.channel.ChannelKeyUtils;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.DispatcherOpEnum;
import com.github.knightliao.vpaas.lc.server.connect.support.log.VpaasServerConnectLogUtils;
import com.github.knightliao.vpaas.lc.server.connect.support.utils.ChannelUtils;
import com.github.knightliao.vpaas.lc.server.server.service.impl.helper.ServerPipeline;
import com.github.knightliao.vpaas.lc.server.session.service.dto.VpaasClientDto;
import com.github.knightliao.vpaas.lc.server.session.service.dto.VpaasCommonUserNameDto;
import com.github.knightliao.vpaas.lc.server.session.service.support.enums.ClientUserLoginoutEnum;
import com.github.knightliao.vpaas.lc.server.session.service.support.enums.VpaasConnectCommonEnum;
import com.github.knightliao.vpaas.lc.server.session.service.support.utils.SessionLogUtils;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnectMessage;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/12 17:04
 */
@Service
public class ConnectHelper {

    @Resource
    private ITokenStoreService tokenStoreService;

    @Resource
    private ISessionStoreService sessionStoreService;

    // ??????????????????
    public ImmutableTriple<SessionStoreDto, SessionStoreDto, VpaasConnectCommonEnum> quickSessionLogin(
            Channel channel,
            VpaasClientDto vpaasClientDto,
            VpaasCommonUserNameDto vpaasCommonUserNameDto,
            MqttConnectMessage mqttConnectMessage) {

        // ?????? ?????????
        int brokerId = LcServerContext.getContext().getBrokerId();
        SessionStoreDto preSessionStoreDto = sessionStoreService.get(brokerId, vpaasClientDto.getClientId());

        // ?????????????????????
        VpaasConnectCommonEnum vpaasConnectCommonEnum;
        if (preSessionStoreDto != null) {
            // ????????????
            SessionStoreDto newSessionStore = getNewSessionStoreDto(preSessionStoreDto, channel,
                    vpaasCommonUserNameDto, mqttConnectMessage);

            // ???????????? channel ??????
            setChannelAttribute(channel, vpaasClientDto.getClientId(), vpaasCommonUserNameDto.getUid(),
                    vpaasCommonUserNameDto.getClientVer());

            // ???????????? channel , ?????????????????????
            // ???????????????????????????
            vpaasConnectCommonEnum = closePreChannel(preSessionStoreDto, channel);

            //
            newSessionStore.setLoginLogoutType(vpaasCommonUserNameDto.getClientUserLoginout().getValue());
            newSessionStore.setConnectType(vpaasConnectCommonEnum.getValue());

            // ??????client???session
            sessionStoreService.put(newSessionStore, getExpireSecond(mqttConnectMessage));

            // ???????????????????????????
            if (vpaasCommonUserNameDto.getClientUserLoginout().equals(ClientUserLoginoutEnum.USER_LOGOUT)) {
                sessionStoreService.removeUser(brokerId, vpaasClientDto.getClientId(),
                        preSessionStoreDto.getSessionStoreKeyDto().getUid());
            }

            return new ImmutableTriple<>(newSessionStore, preSessionStoreDto, vpaasConnectCommonEnum);
        }

        return null;
    }

    // ???????????????????????? ??????????????????
    private SessionStoreDto getNewSessionStoreDto(SessionStoreDto sessionStoreDtoPre, Channel channel,
                                                  VpaasCommonUserNameDto vpaasCommonUserNameDto,
                                                  MqttConnectMessage mqttConnectMessage) {

        SessionStoreDto sessionStoreDtoNew = new SessionStoreDto();

        //
        BeanUtils.copyProperties(sessionStoreDtoPre, sessionStoreDtoNew);

        // ??????uid
        SessionStoreDto.SessionStoreKeyDto sessionStoreKeyDtoPre = sessionStoreDtoPre.getSessionStoreKeyDto();
        sessionStoreDtoNew.getSessionStoreKeyDto().setUid(sessionStoreKeyDtoPre.getUid());
        // ??????ip
        sessionStoreDtoNew.setServerIp(sessionStoreDtoPre.getServerIp());
        //
        sessionStoreDtoNew.setAccessKey(vpaasCommonUserNameDto.getAccessKey());
        sessionStoreDtoNew.setInstanceId(vpaasCommonUserNameDto.getInstanceId());
        sessionStoreDtoNew.setClientVer(vpaasCommonUserNameDto.getClientVer());
        // signtype
        sessionStoreDtoNew.setSignType(vpaasCommonUserNameDto.getSiginType());
        // connect time
        sessionStoreDtoNew.setLastConnectTs(sessionStoreDtoPre.getLastConnectTs());
        sessionStoreDtoNew.setPingTimestamp(sessionStoreDtoPre.getPingTimestamp());
        //
        sessionStoreDtoNew.setExpireSeconds(getExpireSecond(mqttConnectMessage));
        //
        sessionStoreDtoNew.setQuickLoginTimes(sessionStoreDtoPre.getQuickLoginTimes() + 1);

        //
        sessionStoreDtoNew.setChannelLocalId(ChannelKeyUtils.getChannelLocalId(channel));

        return sessionStoreDtoNew;
    }

    private int getExpireSecond(MqttConnectMessage mqttConnectMessage) {

        // ???????????????????????????
        if (mqttConnectMessage.variableHeader().keepAliveTimeSeconds() > 0) {
            return Math.round(mqttConnectMessage.variableHeader().keepAliveTimeSeconds() * 1.5f);
        }

        ILcService lcService = (ILcService) LcServerContext.getContext().getServer();
        return Math.round(lcService.getLcServiceParam().getAllIdleTimeSeconds() * 1.5f);
    }

    public void setChannelAttribute(Channel channel, String clientId, long uid, String clientVer) {

        if (!StringUtils.isEmpty(clientId)) {
            ChannelKeyUtils.setChannelClientSessionAttribute(channel, clientId);
            ChannelKeyUtils.setChannelClientVersionAttribute(channel, clientVer);
        }

        ChannelKeyUtils.setChannelClientSessionUidAttribute(channel, uid);
    }

    //
    // ?????????????????????????????????????????????channel??????(???????????????????????????,??????????????????)????????????????????????channel
    // ?????????channel???????????????????????????
    // ????????????????????? ???????????????????????????
    //
    private VpaasConnectCommonEnum closePreChannel(SessionStoreDto preSessionStoreDto, Channel curChannel) {

        // ???????????? ????????????
        LcWrappedChannel lcWrappedChannelPre =
                LcServerContext.getContext().getServer().getChannel(preSessionStoreDto.getChannelLocalId());

        // ????????? ?????????????????? ???????????????
        if (lcWrappedChannelPre != null) {

            // ??????????????????????????????????????????
            if (preSessionStoreDto.getChannelLocalId()
                    .equalsIgnoreCase(ChannelKeyUtils.getChannelLocalId(curChannel))) {
                return VpaasConnectCommonEnum.CONNECT_QUICK_LOCAL_REPLACE_SELF;
            } else {

                // ?????????????????????, ????????????
                lcWrappedChannelPre.close();
                LcServerContext.getContext().getServer().getChannelGroup().remove(lcWrappedChannelPre);

                //
                VpaasServerConnectLogUtils.doConnectLog(DispatcherOpEnum.closePre, curChannel, 0);

                return VpaasConnectCommonEnum.CONNECT_QUICK_LOCAL_REPLACE;
            }
        }

        // ????????????????????????????????????
        return VpaasConnectCommonEnum.CONNECT_QUICK_REMOTE_REPLACE;
    }

    // ????????????????????????
    public static ClientUserLoginoutEnum getClientUserLoginoutEnum(Channel channel, long uid) {

        long curUid = ChannelKeyUtils.getChannelClientSessionUidAttribute(channel);
        String curClientId = ChannelKeyUtils.getChannelClientSessionAttribute(channel);

        if (StringUtils.isEmpty(curClientId)) {

            // session?????????

            if (uid != PackConstants.DEFAULT_ERROR_UID) {

                // session ????????????client, uid????????????
                return ClientUserLoginoutEnum.CLIENT_USER_LOGIN;
            } else {

                // session????????????client??????
                return ClientUserLoginoutEnum.CLIENT_LOGIN;
            }
        } else {

            // session?????????

            if (uid != PackConstants.DEFAULT_ERROR_UID) {

                // ??????????????????session?????????????????????
                if (curUid != PackConstants.DEFAULT_ERROR_UID) {
                    if (curUid == uid) {
                        // ???????????????
                        return ClientUserLoginoutEnum.USER_LOGIN;
                    } else {
                        // ???????????????
                        return ClientUserLoginoutEnum.USER_REPLACE_LOGIN;
                    }
                } else {

                    // ??????????????????session????????????
                    return ClientUserLoginoutEnum.USER_LOGIN;
                }
            } else {

                // ??????????????? session??????
                if (curUid != PackConstants.DEFAULT_ERROR_UID) {
                    return ClientUserLoginoutEnum.USER_LOGOUT;
                } else {
                    // ??????????????????session???????????????????????????
                    return ClientUserLoginoutEnum.USER_NO_USER_LOGOUT;
                }
            }
        }
    }

    public void saveToSession(Channel channel, VpaasClientDto vpaasClientDto,
                              VpaasCommonUserNameDto vpaasCommonUserNameDto,
                              MqttConnectMessage mqttConnectMessage) {

        int brokerId = LcServerContext.getContext().getBrokerId();

        SessionStoreDto.SessionStoreKeyDto sessionStoreKeyDto = SessionStoreDto.SessionStoreKeyDto
                .builder().clientId(vpaasClientDto.getClientId())
                .brokerId(brokerId)
                .groupKey(vpaasClientDto.getGroupId())
                .uid(vpaasCommonUserNameDto.getUid()).build();

        ILcService lcService = (ILcService) LcServerContext.getContext().getServer();

        SessionStoreDto sessionStoreDto = SessionStoreDto.builder()
                .sessionStoreKeyDto(sessionStoreKeyDto)
                .channelLocalId(ChannelKeyUtils.getChannelLocalId(channel))
                .expireSeconds(getExpireSecond(mqttConnectMessage))

                .serverIp(lcService.getLcServiceParam().getIp())
                .serverPort(lcService.getLcServiceParam().getPort())

                .createTs(System.currentTimeMillis())
                .lastConnectTs(System.currentTimeMillis())
                .pingTimestamp(System.currentTimeMillis())

                .clientVer(vpaasCommonUserNameDto.getClientVer())
                .signType(vpaasCommonUserNameDto.getSiginType())
                .connectType(VpaasConnectCommonEnum.CONNECT_NEW_OK.getValue())
                .clientIp(ChannelUtils.getClientIp(channel))
                .loginLogoutType(vpaasCommonUserNameDto.getClientUserLoginout().getValue())
                .build();

        sessionStoreService.put(sessionStoreDto, getExpireSecond(mqttConnectMessage));
    }

    public void replaceIdleHandler(Channel channel, MqttConnectMessage mqttConnectMessage) {

        if (mqttConnectMessage.variableHeader().keepAliveTimeSeconds() > 0) {

            int expire = Math.round(mqttConnectMessage.variableHeader().keepAliveTimeSeconds() * 1.5f);
            ServerPipeline.replaceIdleHandler(channel, expire);

            //
            SessionLogUtils.doIdleTimeoutLog(channel, expire, ChannelKeyUtils.getChannelClientSessionAttribute(channel));
        }
    }
}
