package com.github.knightliao.vpaas.lc.server.connect.support.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.knightliao.middle.log.LoggerUtil;
import com.github.knightliao.middle.metrics.MonitorHelper;
import com.github.knightliao.middle.thread.MyThreadContext;
import com.github.knightliao.vpaas.lc.server.common.common.constants.VpaasServerConstants;
import com.github.knightliao.vpaas.lc.server.connect.netty.server.LcServerContext;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.channel.ChannelKeyUtils;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.server.ServerLogData;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.DispatcherOpEnum;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.ServerTypeEnum;

import io.netty.channel.Channel;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/6 16:00
 */
public class VpaasServerConnectLogUtils {

    private static Logger LOGGER_CONNECT_OP_LOG = LoggerFactory.getLogger(VpaasServerConstants.LOGGER_CONNECT_OP_LOG);

    public static void doConnectLog(DispatcherOpEnum dispatcherOpEnum, Channel channel, long cost) {

        try {

            //
            MyThreadContext.init();

            if (channel != null) {

                if (isPrintLog()) {
                    LoggerUtil.info(LOGGER_CONNECT_OP_LOG, "{0} {1} {2} {3}",
                            getServerType(),
                            dispatcherOpEnum.getDesc(),
                            ChannelKeyUtils.getChannelClientSessionAttribute(channel), cost);
                } else {

                    setupForLog(channel);
                    LoggerUtil.infoIfNeed(LOGGER_CONNECT_OP_LOG, "{0} {1} {2} {3}",
                            getServerType(),
                            dispatcherOpEnum.getDesc(),
                            ChannelKeyUtils.getChannelClientSessionAttribute(channel), cost);
                }

                // 统计
                MonitorHelper.fastCompassOneKey("LOGGER_CONNECT_OP_LOG", dispatcherOpEnum.getDesc(), 1, cost, true);
            }

        } finally {

            MyThreadContext.clean();

        }
    }

    private static int getServerType() {

        //
        ServerTypeEnum serverTypeEnum = LcServerContext.getContext().getServerTypeEnum();
        if (serverTypeEnum != null) {
            return serverTypeEnum.getValue();
        }
        return ServerTypeEnum.NONE.getValue();
    }

    private static boolean isPrintLog() {

        ServerLogData serverLogData = LcServerContext.getContext().getServerLogData();
        return serverLogData.getLogAllOpen() >= 1;
    }

    private static void setupForLog(Channel channel) {

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
}




