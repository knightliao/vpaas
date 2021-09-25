package com.github.knightliao.vpaas.lc.server.connect.support.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.knightliao.middle.log.LoggerUtil;
import com.github.knightliao.middle.metrics.MonitorHelper;
import com.github.knightliao.middle.thread.MyThreadContext;
import com.github.knightliao.vpaas.lc.server.common.common.constants.VpaasServerConstants;
import com.github.knightliao.vpaas.lc.server.connect.netty.server.LcServerContext;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.channel.ChannelKeyUtils;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.DispatcherOpEnum;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.ServerTypeEnum;
import com.github.knightliao.vpaas.lc.server.connect.support.utils.LogNeedUtils;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/6 16:00
 */
@Slf4j
public class VpaasServerConnectLogUtils {

    private static Logger LOGGER_CONNECT_OP_LOG = LoggerFactory.getLogger(VpaasServerConstants.LOGGER_CONNECT_OP_LOG);

    public static void doConnectLog(DispatcherOpEnum dispatcherOpEnum, Channel channel, long cost) {

        try {

            //
            MyThreadContext.ensureInited();

            if (channel != null) {

                if (LogNeedUtils.isPrintLog()) {
                    LoggerUtil.info(LOGGER_CONNECT_OP_LOG, "{0} {1} {2} {3}",
                            getServerType(),
                            dispatcherOpEnum.getDesc(),
                            ChannelKeyUtils.getChannelClientSessionAttribute(channel), cost);
                } else {

                    boolean isLog = LogNeedUtils.isDoPartOfLog(channel);
                    if (isLog) {
                        LoggerUtil.info(LOGGER_CONNECT_OP_LOG, "{0} {1} {2} {3}",
                                getServerType(),
                                dispatcherOpEnum.getDesc(),
                                ChannelKeyUtils.getChannelClientSessionAttribute(channel), cost);
                    }
                }

                // 统计
                MonitorHelper.fastCompassOneKey("LOGGER_CONNECT_OP_LOG", dispatcherOpEnum.getDesc(), 1, cost, true);
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

}




