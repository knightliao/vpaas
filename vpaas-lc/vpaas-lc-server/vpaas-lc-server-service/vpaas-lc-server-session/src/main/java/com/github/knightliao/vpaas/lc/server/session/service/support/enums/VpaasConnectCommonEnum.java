package com.github.knightliao.vpaas.lc.server.session.service.support.enums;

import lombok.Getter;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/9 21:27
 */
@Getter
public enum VpaasConnectCommonEnum {

    // 1
    CONNECT_NEW_OK("CONNECT_NEW_OK", 0),

    // 2 client id出错
    COMMON_CLIENT_ID_NULL("COMMON_CLIENT_ID_NULL", 10),

    // 3 用户名出错
    COMMON_USERNAME_ERROR("COMMON_USERNAME_ERROR", 11),

    // 4,5 快速连接，需要替换
    CONNECT_QUICK_LOCAL_REPLACE_SELF("CONNECT_QUICK_LOCAL_REPLACE_SELF", 21),
    CONNECT_QUICK_LOCAL_REPLACE("CONNECT_QUICK_LOCAL_REPLACE", 22),
    // 6, 快速连接，
    CONNECT_QUICK_REMOTE_REPLACE("CONNECT_QUICK_REMOTE_REPLACE", 23),

    // 7, 验证出错
    CONNECT_AUTH_FAILED("CONNECT_AUTH_FAILED", 25),

    // 8 未知错误
    CONNECT_UNKNOWN_ERROR("CONNECT_UNKNOWN_ERROR", 26),

    // 9
    CONNECT_TOKEN_EXPIRE("", 30),
    CONNECT_TOKEN_NO_RIGHT("", 31);

    private final String desc;
    private final int value;

    VpaasConnectCommonEnum(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public static VpaasConnectCommonEnum getByValue(Integer input) {
        for (VpaasConnectCommonEnum value : VpaasConnectCommonEnum.values()) {
            if (value.getValue() == input) {
                return value;
            }
        }

        return null;
    }
}
