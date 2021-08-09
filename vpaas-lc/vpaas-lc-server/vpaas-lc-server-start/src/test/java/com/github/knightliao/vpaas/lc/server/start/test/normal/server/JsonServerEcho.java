package com.github.knightliao.vpaas.lc.server.start.test.normal.server;

import com.github.knightliao.vpaas.lc.server.connect.support.enums.SocketType;
import com.github.knightliao.vpaas.lc.server.server.IMyLcServer;
import com.github.knightliao.vpaas.lc.server.session.service.listener.json.JsonEchoMessageEventListener;
import com.github.knightliao.vpaas.lc.server.start.factory.SimpleNewServerFactory;

/**
 * @author knightliao
 * @date 2021/8/7 17:12
 */
public class JsonServerEcho {

    public static void main(String[] args) throws Exception {

        //
        IMyLcServer myLcServer = SimpleNewServerFactory.newServer(0, SocketType.JSON);
        //
        myLcServer.addEventListener(new JsonEchoMessageEventListener());

        myLcServer.bind();
    }
}
