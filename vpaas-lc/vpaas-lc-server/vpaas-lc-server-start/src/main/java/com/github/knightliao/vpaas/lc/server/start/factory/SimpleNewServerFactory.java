package com.github.knightliao.vpaas.lc.server.start.factory;

import com.github.knightliao.vpaas.lc.server.server.service.IMyLcServer;
import com.github.knightliao.vpaas.lc.server.start.service.IServerNewService;
import com.github.knightliao.vpaas.lc.server.start.service.impl.ServerNewServiceImpl;
import com.github.knightliao.vpaas.lc.server.start.support.dto.ServerOptionsDto;

/**
 * @author knightliao
 * @date 2021/8/7 17:24
 */
public class SimpleNewServerFactory {

    private static IServerNewService serverNewService = new ServerNewServiceImpl();

    public static IMyLcServer newServer(int brokerId) {

        //
        ServerOptionsDto serverOptionsDto = new ServerOptionsDto();
        serverOptionsDto.setBrokerId(brokerId);
        serverOptionsDto.setPort(8000);

        //
        IMyLcServer server = (IMyLcServer) serverNewService.newServer(serverOptionsDto);

        return server;
    }
}
