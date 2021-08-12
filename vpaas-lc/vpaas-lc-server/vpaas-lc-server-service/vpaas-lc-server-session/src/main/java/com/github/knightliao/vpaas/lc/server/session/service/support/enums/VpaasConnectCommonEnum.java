package com.github.knightliao.vpaas.lc.server.session.service.support.enums;

import lombok.Getter;

/**
 * @author knightliao
 * @date 2021/8/9 21:27
 */
@Getter
public enum VpaasConnectCommonEnum {

    // client id出错
    COMMON_CLIENT_ID_NULL("COMMON_CLIENT_ID_NULL", 0),

    // 用户名出错
    COMMON_USERNAME_ERROR("COMMON_USERNAME_ERROR", 1);

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
