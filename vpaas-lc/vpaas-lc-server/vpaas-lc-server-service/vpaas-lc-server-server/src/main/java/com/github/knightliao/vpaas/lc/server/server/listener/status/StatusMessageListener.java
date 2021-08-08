package com.github.knightliao.vpaas.lc.server.server.listener.status;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.netty.listener.LcMessageEventListener;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.LcEventBehaviorEnum;
import com.github.knightliao.vpaas.lc.server.server.listener.status.helper.LcServerStatusHelper;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author knightliao
 * @date 2021/8/7 18:25
 */
public class StatusMessageListener implements LcMessageEventListener {

    private static final String CMD_STATUS = "get status";
    private static final String CMD_EXECUTORS = "get exec";
    private static final String CMD_CONFIG = "get config";
    private static final String CMD_QUIT = "quit";
    private static final String CMD_EXIT = "exit";

    @Override
    public LcEventBehaviorEnum channelRead(ChannelHandlerContext ctx, LcWrappedChannel channel, Object msg) {

        if (msg instanceof String) {

            String command = (String) msg;

            if (!StringUtils.isBlank((command))) {

                Map<String, Object> resultMap = null;
                if (command.equalsIgnoreCase(CMD_STATUS)) {
                    resultMap = LcServerStatusHelper.doGetStatus();
                }

                //
                channel.writeAndFlush(LcServerStatusHelper.formatResultMap(resultMap), false);
            }

        }

        return LcEventBehaviorEnum.CONTINUE;
    }
}
