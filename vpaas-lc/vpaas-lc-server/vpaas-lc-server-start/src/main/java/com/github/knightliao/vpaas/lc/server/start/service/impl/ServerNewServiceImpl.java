package com.github.knightliao.vpaas.lc.server.start.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.github.knightliao.middle.utils.net.IpUtils;
import com.github.knightliao.vpaas.lc.server.common.common.constants.VpaasServerConstants;
import com.github.knightliao.vpaas.lc.server.connect.netty.server.ILcServer;
import com.github.knightliao.vpaas.lc.server.connect.netty.service.ILcService;
import com.github.knightliao.vpaas.lc.server.server.IMyLcServer;
import com.github.knightliao.vpaas.lc.server.server.service.impl.LcServerImpl;
import com.github.knightliao.vpaas.lc.server.start.service.IServerNewService;
import com.github.knightliao.vpaas.lc.server.start.support.dto.ServerOptionsDto;

import io.netty.channel.ChannelHandler;

/**
 * @author knightliao
 * @date 2021/8/7 17:26
 */
public class ServerNewServiceImpl implements IServerNewService {

    @Override
    public ILcServer newServer(ServerOptionsDto serverOptionsDto, Map<String, ChannelHandler> mapHandlers) {

        // server
        ILcServer server = innerNewServer(serverOptionsDto);

        // handler
        for (String handlerKey : mapHandlers.keySet()) {
            server.addChannelHandler(handlerKey, mapHandlers.get(handlerKey));
        }

        return server;
    }

    @Override
    public ILcServer newServer(ServerOptionsDto serverOptionsDto) {
        return newServer(serverOptionsDto, new HashMap<>());
    }

    private ILcServer innerNewServer(ServerOptionsDto serverOptionsDto) {

        if (serverOptionsDto == null) {
            throw new NullPointerException("serverOptionsDto is null");
        }

        // new
        IMyLcServer server = new LcServerImpl();
        ILcService service = (ILcService) server;

        // broker name
        if (!StringUtils.isEmpty(serverOptionsDto.getBrokerName())) {
            server.getServerParam().setBrokerName(serverOptionsDto.getBrokerName());
        } else {
            server.getServerParam().setBrokerName(VpaasServerConstants.DEFAULT_SERVER_NAME);
        }

        // broker id
        if (serverOptionsDto.getBrokerId() != null) {
            server.getServerParam().setBrokerId(serverOptionsDto.getBrokerId());
        } else {
            server.getServerParam().setBrokerId(VpaasServerConstants.DEFAULT_SERVER_ID);
        }

        // common
        service.getLcServiceParam().setPort(serverOptionsDto.getPort());
        service.getLcServiceParam().setIp(IpUtils.getLocalIp());
        service.getLcServiceParam().setKeepAlive(serverOptionsDto.isKeepAlive());
        service.getLcServiceParam().setCorePoolSize(serverOptionsDto.getCorePoolSize());
        service.getLcServiceParam().setMaximumPoolSize(serverOptionsDto.getMaxmumPoolSize());
        service.getLcServiceParam().setQueueCapacity(serverOptionsDto.getQueueCapacity());
        service.getLcServiceParam().setSocketType(serverOptionsDto.getSocketType());

        // status
        server.getServerParam().setOpenStatus(serverOptionsDto.isOpenStatus());
        server.getServerParam().setStatusPort(serverOptionsDto.getStatusPort());

        // worker & boss
        service.getLcServiceParam().setWorkerCount(serverOptionsDto.getWorkCount());
        service.getLcServiceParam().setBossCount(serverOptionsDto.getBossCount());

        //
        return server;
    }
}
