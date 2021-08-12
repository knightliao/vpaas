package com.github.knightliao.vpaas.lc.server.connect.support.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.knightliao.middle.log.LoggerUtil;
import com.github.knightliao.vpaas.lc.server.common.common.constants.VpaasServerConstants;
import com.github.knightliao.vpaas.lc.server.connect.netty.server.LcServerContext;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.channel.ChannelKeyUtils;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.server.ServerLogData;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.DispatcherOpEnum;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.ServerTypeEnum;

import io.netty.channel.Channel;

/**
 * @author knightliao
 * @date 2021/8/6 16:00
 */
public class VpaasServerConnectLogUtils {

    private static Logger LOGGER_CONNECT_OP_LOG = LoggerFactory.getLogger(VpaasServerConstants.LOGGER_CONNECT_OP_LOG);

    public static void doConnectLog(DispatcherOpEnum dispatcherOpEnum, Channel channel, long cost) {

        try {

            if (channel != null) {

                if (isPrintLog()) {
                    LoggerUtil.info(LOGGER_CONNECT_OP_LOG, "{0} {1} {2} {3}",
                            getServerType(),
                            dispatcherOpEnum.getDesc(),
                            ChannelKeyUtils.getChannelClientSessionAttribute(channel), cost);
                } else {
                    LoggerUtil.info(LOGGER_CONNECT_OP_LOG, "{0} {1} {2} {3}",
                            getServerType(),
                            dispatcherOpEnum.getDesc(),
                            ChannelKeyUtils.getChannelClientSessionAttribute(channel), cost);
                }
            }

        } finally {

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
}




