package com.github.knightliao.vpaas.lc.server.router.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.knightliao.vpaas.api.support.enums.VpaasLcOfflineEnum;
import com.github.knightliao.vpaas.api.web.request.VpaasOfflineRequest;
import com.github.knightliao.vpaas.common.store.ISessionStoreService;
import com.github.knightliao.vpaas.common.store.dto.SessionStoreDto;
import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.netty.server.LcServerContext;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.channel.ChannelKeyUtils;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.DispatcherOpEnum;
import com.github.knightliao.vpaas.lc.server.router.service.VpaasStatusOpService;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/22 15:21
 */
@Service
public class VpaasStatusOpServiceImpl implements VpaasStatusOpService {

    @Resource
    private ISessionStoreService sessionStoreService;

    @Override
    public VpaasLcOfflineEnum offline(VpaasOfflineRequest vpaasOfflineRequest) {

        int brokerId = LcServerContext.getContext().getBrokerId();

        VpaasLcOfflineEnum clientRemove = offlineClientId(brokerId, vpaasOfflineRequest);

        return clientRemove;
    }

    private VpaasLcOfflineEnum offlineClientId(int brokerId, VpaasOfflineRequest vpaasOfflineRequest) {

        SessionStoreDto sessionStoreDto = sessionStoreService.get(brokerId, vpaasOfflineRequest.getClientId());
        if (sessionStoreDto != null) {

            sessionStoreService.remove(brokerId, vpaasOfflineRequest.getClientId());

            //
            boolean channelOffline = offlineChannel(sessionStoreDto.getChannelLocalId());
            if (channelOffline) {
                return VpaasLcOfflineEnum.CLIENT_OFFLINE;
            }
            return VpaasLcOfflineEnum.CHANNEL_NULL_IN_MAP;
        }

        return VpaasLcOfflineEnum.SESSION_NOT_EXIST;
    }

    private boolean offlineChannel(String channelId) {

        //
        LcWrappedChannel channel = LcServerContext.getContext().getServer().getChannel(channelId);
        if (channel == null) {
            return false;
        }
        channel.close();

        //
        ChannelKeyUtils.setChannelClientInactiveAttribute(channel, DispatcherOpEnum.forceOffline.getValue());

        return true;
    }
}
