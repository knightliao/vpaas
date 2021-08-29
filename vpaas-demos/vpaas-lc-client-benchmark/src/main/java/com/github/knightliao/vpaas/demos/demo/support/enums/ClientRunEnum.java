package com.github.knightliao.vpaas.demos.demo.support.enums;

import lombok.Getter;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/29 01:45
 */
@Getter
public enum ClientRunEnum {

    SINGLE("SINGLE", 0),
    MESS("MESS", 1);

    private final String desc;
    private final int value;

    ClientRunEnum(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public static ClientRunEnum getByValue(Integer input) {
        for (ClientRunEnum value : ClientRunEnum.values()) {
            if (value.getValue() == input) {
                return value;
            }
        }

        return null;
    }
}
