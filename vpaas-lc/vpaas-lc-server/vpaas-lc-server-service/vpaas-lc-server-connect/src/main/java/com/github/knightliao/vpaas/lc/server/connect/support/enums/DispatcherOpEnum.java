package com.github.knightliao.vpaas.lc.server.connect.support.enums;

import lombok.Getter;

/**
 * @author knightliao
 * @date 2021/8/6 20:57
 */
@Getter
public enum DispatcherOpEnum {

    channelRead("channelRead", 0),
    channelActive("channelActive", 1),
    channelInActive("channelInActive", 2),

    exceptionCaughtIo("exceptionCaughtIo", 3),
    exceptionCaught("exceptionCaught", 4),

    heartbeat("heartbeat", 5),

    readTimeout("readTimeout", 6),
    allTimeout("allTimeout", 7),
    writeTimeout("writeTimeout", 8),

    pingClose("pingClose", 9),
    closePre("closePre", 10),

    disconnect("disconnect", 11),
    forceOffline("forceOffline", 12),

    connectClose_AUTH_FAILED("connectClose_AUTH_FAILED", 100),
    connectClose_CLIENT_ID_NULL("connectClose_CLIENT_ID_NULL", 101),
    connectClose_USERNAME_ERROR("connectClose_USERNAME_ERROR", 102),
    connectClose_OTHERS("connectClose_OTHERS", 103),

    connectClose_TOKEN_AUTH_FAILED("connectClose_TOKEN_AUTH_FAILED", 110),
    connectClose_TOKEN_USERNAME_ERROR("connectClose_TOKEN_USERNAME_ERROR", 111),
    connectClose_TOKEN_OTHERS("connectClose_TOKEN_OTHERS", 112);

    private final String desc;
    private final int value;

    DispatcherOpEnum(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public static DispatcherOpEnum getByValue(Integer input) {
        for (DispatcherOpEnum value : DispatcherOpEnum.values()) {
            if (value.getValue() == input) {
                return value;
            }
        }

        return null;
    }
}
