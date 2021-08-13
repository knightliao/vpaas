package com.github.knightliao.vpaas.lc.server.session.service.support.helper;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.knightliao.vpaas.common.store.ISessionStoreService;
import com.github.knightliao.vpaas.lc.server.connect.netty.server.LcServerContext;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.channel.ChannelKeyUtils;

import io.netty.channel.Channel;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/13 17:21
 */
@Service
public class VpaasSessionDisconnectHelper {

    @Resource
    private ISessionStoreService sessionStoreService;

    public void disconnect(Channel channel) {

        int brokerId = LcServerContext.getContext().getBrokerId();
        String clientId = ChannelKeyUtils.getChannelClientSessionAttribute(channel);

        // 如果该client并没有建立好client连接，则client为空，这里不需要记录
        if (clientId == null) {
            return;
        }

        //
        sessionStoreService.remove(brokerId, clientId);
    }
}
