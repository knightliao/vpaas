package com.github.knightliao.vpaas.lc.server.connect.support.utils;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import io.netty.channel.Channel;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/13 11:25
 */
public class ChannelUtils {

    public static String getClientIp(Channel channel) {

        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        if (socketAddress == null) {
            return "";
        }

        InetAddress inetAddress = socketAddress.getAddress();
        return inetAddress.getHostAddress();
    }
}
