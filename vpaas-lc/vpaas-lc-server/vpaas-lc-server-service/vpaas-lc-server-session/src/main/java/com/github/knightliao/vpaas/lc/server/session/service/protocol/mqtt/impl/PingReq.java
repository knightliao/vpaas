package com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.knightliao.vpaas.common.store.ISessionStoreService;
import com.github.knightliao.vpaas.common.store.dto.SessionStoreDto;
import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.netty.server.LcServerContext;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.channel.ChannelKeyUtils;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.DispatcherOpEnum;
import com.github.knightliao.vpaas.lc.server.connect.support.log.VpaasServerConnectLogUtils;
import com.github.knightliao.vpaas.lc.server.session.service.protocol.IProtocolProcessor;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/13 14:35
 */
@Service(value = "PingReq")
public class PingReq implements IProtocolProcessor {

    @Resource
    private ISessionStoreService sessionStoreService;

    @Override
    public void doPre(Channel channel, Object msg) {

    }

    @Override
    public void doAfter(Channel channel, Object msg) {

    }

    @Override
    public void process(Channel channel, Object msg) {

        MqttMessage mqttMessage = (MqttMessage) msg;
        doPing(channel);
    }

    @Override
    public boolean canGo(Object msg) {
        return msg instanceof MqttMessage;
    }

    private void doPing(Channel channel) {

        String clientId = ChannelKeyUtils.getChannelClientSessionAttribute(channel);
        if (clientId != null) {

            // 获取会话信息
            SessionStoreDto sessionStoreDto = sessionStoreService.get(LcServerContext.getContext().getBrokerId(),
                    clientId);

            if (sessionStoreDto != null) {

                if (channel.id().asShortText().equals(sessionStoreDto.getChannelLocalId())) {

                    LcWrappedChannel lcWrappedChannel =
                            LcServerContext.getContext().getServer().getChannelByGroup(channel.id());
                    int brokerId = LcServerContext.getContext().getBrokerId();

                    if (brokerId == sessionStoreDto.getSessionStoreKeyDto().getBrokerId() && lcWrappedChannel != null) {

                        // 延续超时
                        sessionStoreDto.setPingTimestamp(System.currentTimeMillis());
                        sessionStoreService.put(sessionStoreDto, sessionStoreDto.getExpireSeconds());

                        // 返回值
                        doResponse(channel);
                        return;
                    }
                }
            }
        }

        // 没有会话，断开连接吧
        VpaasServerConnectLogUtils.doConnectLog(DispatcherOpEnum.pingClose, channel, 0);
        ChannelKeyUtils.setChannelClientInactiveAttribute(channel, DispatcherOpEnum.pingClose.getValue());
    }

    private void doResponse(Channel channel) {

        MqttMessage pingRespMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0), null, null);
        channel.writeAndFlush(pingRespMessage);
    }
}
