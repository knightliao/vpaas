package com.github.knightliao.vpaas.api.support.enums;

import lombok.Getter;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/22 15:14
 */
@Getter
public enum VpaasLcOfflineEnum {

    CLIENT_OFFLINE("CLIENT_OFFLINE", 0),
    CHANNEL_NULL_IN_MAP("CHANNEL_NULL_IN_MAP", 1),
    SESSION_NOT_EXIST("SESSION_NOT_EXIST", 2);

    private final String desc;
    private final int value;

    VpaasLcOfflineEnum(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public static VpaasLcOfflineEnum getByValue(Integer input) {
        for (VpaasLcOfflineEnum value : VpaasLcOfflineEnum.values()) {
            if (value.getValue() == input) {
                return value;
            }
        }

        return null;
    }
}
