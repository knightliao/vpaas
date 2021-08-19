package com.github.knightliao.vpaas.lc.server.connect.support.utils;

import com.github.knightliao.middle.thread.MyThreadContext;
import com.github.knightliao.vpaas.lc.server.connect.netty.server.LcServerContext;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.channel.ChannelKeyUtils;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.server.ServerLogData;

import io.netty.channel.Channel;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/20 00:03
 */
public class LogNeedUtils {

    public static boolean isPrintLog() {

        ServerLogData serverLogData = LcServerContext.getContext().getServerLogData();
        return serverLogData.getLogAllOpen() >= 1;
    }

    public static void setupForLog(Channel channel) {

        long uid = ChannelKeyUtils.getChannelClientSessionUidAttribute(channel);
        String clientId = ChannelKeyUtils.getChannelClientSessionAttribute(channel);

        boolean printLog = false;
        ServerLogData serverLogData = LcServerContext.getContext().getServerLogData();
        ;
        if (serverLogData.getLogClients().contains(clientId)) {
            printLog = true;
        }
        if (serverLogData.getLogUids().contains(uid)) {
            printLog = true;
        }

        MyThreadContext.putPrintLogKey(printLog);
    }

    public static void setupForLog(String clientId, long uid) {

        boolean printLog = false;
        ServerLogData serverLogData = LcServerContext.getContext().getServerLogData();
        ;
        if (serverLogData.getLogClients().contains(clientId)) {
            printLog = true;
        }
        if (serverLogData.getLogUids().contains(uid)) {
            printLog = true;
        }

        MyThreadContext.putPrintLogKey(printLog);
    }
}
