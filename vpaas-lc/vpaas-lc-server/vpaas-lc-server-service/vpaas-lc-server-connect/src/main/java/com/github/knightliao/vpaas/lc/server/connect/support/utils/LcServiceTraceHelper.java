package com.github.knightliao.vpaas.lc.server.connect.support.utils;

import org.slf4j.MDC;

import com.github.knightliao.middle.trace.MyTraceUtils;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.channel.ChannelKeyUtils;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/5 00:08
 */
@Slf4j
public class LcServiceTraceHelper {

    private final static String CHANNEL_ID = "channelId";
    private final static String CLIENT_ID = "clientId";
    private final static String UID = "uid";

    public static void startTraceAndSession(final Channel channel) {

        // get session
        fromChannelSession(channel);

        // get trace
        newChannelRequest();
    }

    public static void stopTrace() {

    }

    private static void newChannelRequest(){

        MyTraceUtils.newTrace();
    }

    public static void clearChannelSession() {
        try {

            //
            MDC.put(CHANNEL_ID, "");

            MDC.put(CLIENT_ID, "");
            MDC.put(UID, "");
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }
    }

    private static void fromChannelSession(Channel channel) {

        try {

            //
            MDC.put(CHANNEL_ID, channel.id().asShortText());

            MDC.put(CLIENT_ID, getClientId(channel));
            MDC.put(UID, getUid(channel));

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }
    }

    private static String getClientId(Channel channel) {

        String clientId = ChannelKeyUtils.getChannelClientSessionAttribute(channel);

        return clientId == null ? "" : clientId;
    }

    private static String getUid(Channel channel) {

        long uid = ChannelKeyUtils.getChannelClientSessionUidAttribute(channel);
        return String.valueOf(uid);
    }
}
