package com.github.knightliao.vpaas.lc.server.connect.support.enums;

import lombok.Getter;

/**
 * @author knightliao
 * @email knightliao@gmail.com
* @email knightliao@gmail.com
 * @date 2021/8/9 21:27
 */
@Getter
public enum ServerTypeEnum {

    NONE("NONE", 0),
    SERVER("SERVER", 1),
    CLIENT("CLIENT", 2);

    private final String desc;
    private final int value;

    ServerTypeEnum(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public static ServerTypeEnum getByValue(Integer input) {
        for (ServerTypeEnum value : ServerTypeEnum.values()) {
            if (value.getValue() == input) {
                return value;
            }
        }

        return null;
    }
}
