package com.github.knightliao.vpaas.demos.demo.support.enums;

import lombok.Getter;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/29 01:45
 */
@Getter
public enum ClientRunEnums {

    SINGLE("SINGLE", 0),
    MESS("MESS", 1);

    private final String desc;
    private final int value;

    ClientRunEnums(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public static ClientRunEnums getByValue(Integer input) {
        for (ClientRunEnums value : ClientRunEnums.values()) {
            if (value.getValue() == input) {
                return value;
            }
        }

        return null;
    }
}
