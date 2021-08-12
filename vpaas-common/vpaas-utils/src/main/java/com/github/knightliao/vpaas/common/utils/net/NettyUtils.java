package com.github.knightliao.vpaas.common.utils.net;

import org.apache.commons.lang3.StringUtils;

import io.netty.channel.epoll.Epoll;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/5 21:49
 */
public class NettyUtils {

    public static boolean useEpoll() {

        String osName = System.getProperty("os.name");
        boolean isLinuxPlatform = StringUtils.containsIgnoreCase(osName, "linux");
        return isLinuxPlatform && Epoll.isAvailable();
    }
}
