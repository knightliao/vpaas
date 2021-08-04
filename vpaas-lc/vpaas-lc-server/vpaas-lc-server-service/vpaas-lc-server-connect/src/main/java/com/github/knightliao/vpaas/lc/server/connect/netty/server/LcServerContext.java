package com.github.knightliao.vpaas.lc.server.connect.netty.server;

import com.github.knightliao.vpaas.lc.server.connect.support.dto.server.ServerLogData;

import lombok.Data;

/**
 * @author knightliao
 * @date 2021/8/4 22:48
 */
@Data
public class LcServerContext {

    private ILcServer server;
    private int brokerId;
    private ServerLogData serverLogData;

    private static LcServerContext lcServerContext = new LcServerContext();

    private LcServerContext() {
        serverLogData = new ServerLogData();
    }

    public static LcServerContext getContext() {
        return lcServerContext;
    }
}
