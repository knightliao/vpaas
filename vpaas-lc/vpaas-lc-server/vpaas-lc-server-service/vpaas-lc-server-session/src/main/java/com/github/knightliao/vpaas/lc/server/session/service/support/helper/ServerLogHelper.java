package com.github.knightliao.vpaas.lc.server.session.service.support.helper;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.knightliao.middle.thread.MyThreadContext;
import com.github.knightliao.vpaas.common.rely.config.VpaasConfig;
import com.github.knightliao.vpaas.lc.server.connect.netty.server.LcServerContext;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.channel.ChannelKeyUtils;

import io.netty.channel.Channel;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/19 20:01
 */
@Service
public class ServerLogHelper {

    @Resource
    private VpaasConfig vpaasConfig;

    public void setUpForLog(Channel channel) {

        //
        long uid = ChannelKeyUtils.getChannelClientSessionUidAttribute(channel);
        String clientId = ChannelKeyUtils.getChannelClientSessionAttribute(channel);
        MyThreadContext.putPrintLogKey(vpaasConfig.isPrintLog(clientId, uid));

        //
        putLogDataToContext();
    }

    public void putLogDataToContext() {

        Set<String> clients = vpaasConfig.getLogClientSet();
        Set<Long> uids = vpaasConfig.getLogUidSet();

        LcServerContext.getContext().getServerLogData().setLogClients(clients);
        LcServerContext.getContext().getServerLogData().setLogUids(uids);
    }
}
