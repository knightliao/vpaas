package com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.helper;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.github.knightliao.vpaas.common.basic.constants.VpaasConstants;
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

    // 进行快速登录
    public ImmutableTriple<SessionStoreDto, SessionStoreDto, VpaasConnectCommonEnum> quickSessionLogin(
            Channel channel,
            VpaasClientDto vpaasClientDto,
            VpaasCommonUserNameDto vpaasCommonUserNameDto,
            MqttConnectMessage mqttConnectMessage) {

        // 获取 前会话
        int brokerId = LcServerContext.getContext().getBrokerId();
        SessionStoreDto preSessionStoreDto = sessionStoreService.get(brokerId, vpaasClientDto.getClientId());

        // 前会话已经存在
        VpaasConnectCommonEnum vpaasConnectCommonEnum;
        if (preSessionStoreDto != null) {
            // 覆盖内容
            SessionStoreDto newSessionStore = getNewSessionStoreDto(preSessionStoreDto, channel,
                    vpaasCommonUserNameDto, mqttConnectMessage);

            // 设置当前 channel 属性
            setChannelAttribute(channel, vpaasClientDto.getClientId(), vpaasCommonUserNameDto.getUid(),
                    vpaasCommonUserNameDto.getClientVer());

            // 释放以前 channel , 如果是本机的话
            // 如果不是本机则忽略
            vpaasConnectCommonEnum = closePreChannel(preSessionStoreDto, channel);

            //
            newSessionStore.setLoginLogoutType(vpaasCommonUserNameDto.getClientUserLoginout().getValue());
            newSessionStore.setConnectType(vpaasConnectCommonEnum.getValue());

            // 更新client的session
            sessionStoreService.put(newSessionStore, getExpireSecond(mqttConnectMessage));

            // 如果用户要退出的话
            if (vpaasCommonUserNameDto.getClientUserLoginout().equals(ClientUserLoginoutEnum.USER_LOGOUT)) {
                sessionStoreService.removeUser(brokerId, vpaasClientDto.getClientId(),
                        preSessionStoreDto.getSessionStoreKeyDto().getUid());
            }

            return new ImmutableTriple<>(newSessionStore, preSessionStoreDto, vpaasConnectCommonEnum);
        }

        return null;
    }

    // 快速连接时，需要 设置新的会话
    private SessionStoreDto getNewSessionStoreDto(SessionStoreDto sessionStoreDtoPre, Channel channel,
                                                  VpaasCommonUserNameDto vpaasCommonUserNameDto,
                                                  MqttConnectMessage mqttConnectMessage) {

        SessionStoreDto sessionStoreDtoNew = new SessionStoreDto();

        //
        BeanUtils.copyProperties(sessionStoreDtoPre, sessionStoreDtoNew);

        // 更换uid
        SessionStoreDto.SessionStoreKeyDto sessionStoreKeyDtoPre = sessionStoreDtoPre.getSessionStoreKeyDto();
        sessionStoreDtoNew.getSessionStoreKeyDto().setUid(sessionStoreKeyDtoPre.getUid());
        // 更换ip
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

        // 以客户端输入的为准
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
    // 会话续存的情况下，需要把以前的channel干掉(应该是以前的过期了,但还未被删除)，然后开启当前的channel
    // 以前的channel也要在本机才支持的
    // 不是本机的则由 异步服务来进行路踢
    //
    private VpaasConnectCommonEnum closePreChannel(SessionStoreDto preSessionStoreDto, Channel curChannel) {

        // 获取本机 别的连接
        LcWrappedChannel lcWrappedChannelPre =
                LcServerContext.getContext().getServer().getChannel(preSessionStoreDto.getChannelLocalId());

        // 在本机 并且这个连接 不是当前的
        if (lcWrappedChannelPre != null) {

            // 两个为同一个连接，不需要替换
            if (preSessionStoreDto.getChannelLocalId()
                    .equalsIgnoreCase(ChannelKeyUtils.getChannelLocalId(curChannel))) {
                return VpaasConnectCommonEnum.CONNECT_QUICK_LOCAL_REPLACE_SELF;
            } else {

                // 本机的其它连接, 需要替换
                lcWrappedChannelPre.close();
                LcServerContext.getContext().getServer().getChannelGroup().remove(lcWrappedChannelPre);

                //
                VpaasServerConnectLogUtils.doConnectLog(DispatcherOpEnum.closePre, curChannel, 0);

                return VpaasConnectCommonEnum.CONNECT_QUICK_LOCAL_REPLACE;
            }
        }

        // 非本机上的，可能是远程的
        return VpaasConnectCommonEnum.CONNECT_QUICK_REMOTE_REPLACE;
    }

    // 获取用户登录方式
    public static ClientUserLoginoutEnum getClientUserLoginoutEnum(Channel channel, long uid) {

        long curUid = ChannelKeyUtils.getChannelClientSessionUidAttribute(channel);
        String curClientId = ChannelKeyUtils.getChannelClientSessionAttribute(channel);

        if (StringUtils.isEmpty(curClientId)) {

            // session无数据

            if (uid != VpaasConstants.DEFAULT_ERROR_UID) {

                // session 无数据，client, uid同时登录
                return ClientUserLoginoutEnum.CLIENT_USER_LOGIN;
            } else {

                // session无数据，client登录
                return ClientUserLoginoutEnum.CLIENT_LOGIN;
            }
        } else {

            // session有数据

            if (uid != VpaasConstants.DEFAULT_ERROR_UID) {

                // 用户要登录，session中有，重复登录
                if (curUid != VpaasConstants.DEFAULT_ERROR_UID) {
                    if (curUid == uid) {
                        // 本用户登录
                        return ClientUserLoginoutEnum.USER_LOGIN;
                    } else {
                        // 换用户登录
                        return ClientUserLoginoutEnum.USER_REPLACE_LOGIN;
                    }
                } else {

                    // 用户要登录，session中无用户
                    return ClientUserLoginoutEnum.USER_LOGIN;
                }
            } else {

                // 用户要登出 session中有
                if (curUid != VpaasConstants.DEFAULT_ERROR_UID) {
                    return ClientUserLoginoutEnum.USER_LOGOUT;
                } else {
                    // 用户要登出，session中无用户，重复登出
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
        }
    }
}
