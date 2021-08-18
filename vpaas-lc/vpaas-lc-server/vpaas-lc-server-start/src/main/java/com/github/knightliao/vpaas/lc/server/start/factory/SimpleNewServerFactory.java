package com.github.knightliao.vpaas.lc.server.start.factory;

import com.github.knightliao.vpaas.lc.server.connect.support.enums.SocketType;
import com.github.knightliao.vpaas.lc.server.server.IMyLcServer;
import com.github.knightliao.vpaas.lc.server.start.service.IServerNewService;
import com.github.knightliao.vpaas.lc.server.start.service.impl.ServerNewServiceImpl;
import com.github.knightliao.vpaas.lc.server.start.support.dto.ServerOptionsDto;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/7 17:24
 */
public class SimpleNewServerFactory {

    private static IServerNewService serverNewService = new ServerNewServiceImpl();

    public static IMyLcServer newServer(int brokerId, int port, int statusPort) {

        //
        ServerOptionsDto serverOptionsDto = new ServerOptionsDto();
        serverOptionsDto.setBrokerId(brokerId);
        serverOptionsDto.setPort(port);
        serverOptionsDto.setStatusPort(statusPort);
        serverOptionsDto.setSocketType(SocketType.MQTT);

        //
        IMyLcServer server = (IMyLcServer) serverNewService.newServer(serverOptionsDto);

        return server;
    }

    public static IMyLcServer newServer(int brokerId, int port, int statusPort, SocketType socketType) {

        //
        ServerOptionsDto serverOptionsDto = new ServerOptionsDto();
        serverOptionsDto.setBrokerId(brokerId);
        serverOptionsDto.setPort(port);
        serverOptionsDto.setStatusPort(statusPort);
        serverOptionsDto.setSocketType(socketType);

        //
        IMyLcServer server = (IMyLcServer) serverNewService.newServer(serverOptionsDto);

        return server;
    }
}
