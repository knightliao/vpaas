package com.github.knightliao.vpaas.lc.server.start.test.normal.server;

import com.github.knightliao.vpaas.lc.server.connect.support.enums.SocketType;
import com.github.knightliao.vpaas.lc.server.server.service.IMyLcServer;
import com.github.knightliao.vpaas.lc.server.session.service.listener.json.JsonEchoMessageEventListener;
import com.github.knightliao.vpaas.lc.server.start.factory.SimpleNewServerFactory;
import com.github.knightliao.vpaas.lc.server.start.support.dto.ServerOptionsDto;

/**
 * @author knightliao
 * @date 2021/8/7 17:12
 */
public class JsonServerEcho {

    public static void main(String[] args) throws Exception {

        ServerOptionsDto serverOptionsDto = new ServerOptionsDto();
        serverOptionsDto.setPort(7000);
        serverOptionsDto.setSocketType(SocketType.JSON);

        //
        IMyLcServer myLcServer = SimpleNewServerFactory.newServer(0);
        //
        myLcServer.addEventListener(new JsonEchoMessageEventListener());

        myLcServer.bind();
    }
}
