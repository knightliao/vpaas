package com.github.knightliao.vpaas.lc.server.connect.support.dto.channel;

import com.github.knightliao.vpaas.common.basic.constants.VpaasConstants;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * @author knightliao
 * @date 2021/8/7 09:43
 */
public class ChannelKeyUtils {

    private static final String CHANNEL_CLIENT_ID = "clientId";
    private static final String CHANNEL_CLIENT_VERSION = "ver";
    private static final String CHANNEL_CLIENT_ID_TOKEN = "token";
    private static final String CHANNEL_UID = "uid";

    private static final String CHANNEL_INACTIVE_TYPE = "inactiveType";

    public static String getChannelLocalId(Channel chanel) {
        return chanel.id().asShortText();
    }

    // client
    public static void setChannelClientSessionAttribute(Channel channel, String clientId) {
        channel.attr(AttributeKey.valueOf(CHANNEL_CLIENT_ID)).set(clientId);
    }

    public static String getChannelClientSessionAttribute(Channel channel) {
        return (String) channel.attr(AttributeKey.valueOf(CHANNEL_CLIENT_ID)).get();
    }

    // uid
    public static void setChannelClientSessionUidAttribute(Channel channel, long uid) {
        channel.attr(AttributeKey.valueOf(CHANNEL_UID)).set(uid);
    }

    public static long getChannelClientSessionUidAttribute(Channel channel) {
        Long ret = (Long) channel.attr(AttributeKey.valueOf(CHANNEL_UID)).get();
        if (ret == null) {
            return VpaasConstants.DEFAULT_ERROR_VALUE_LONG;
        }

        return ret;
    }

    // inactive type
    public static void setChannelClientInactiveAttribute(Channel channel, int type) {
        channel.attr(AttributeKey.valueOf(CHANNEL_INACTIVE_TYPE)).set(type);
    }

    public static int getChannelClientInactiveAttribute(Channel channel) {
        Integer ret = (Integer) channel.attr(AttributeKey.valueOf(CHANNEL_INACTIVE_TYPE)).get();
        if (ret == null) {
            return VpaasConstants.DEFAULT_ERROR_VALUE_INT;
        }

        return ret;
    }
}
