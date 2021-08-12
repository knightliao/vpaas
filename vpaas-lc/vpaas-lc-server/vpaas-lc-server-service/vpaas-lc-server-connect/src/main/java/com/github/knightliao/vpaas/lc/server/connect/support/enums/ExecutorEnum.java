package com.github.knightliao.vpaas.lc.server.connect.support.enums;

import lombok.Getter;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/4 23:34
 */
@Getter
public enum ExecutorEnum {

    MESSAGE("MESSAGE", 0),
    CHANNEL("CHANNEL", 1),
    EXCEPTION("EXCEPTION", 2);

    private final String desc;
    private final int value;

    ExecutorEnum(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public static ExecutorEnum getByValue(Integer input) {
        for (ExecutorEnum value : ExecutorEnum.values()) {
            if (value.getValue() == input) {
                return value;
            }
        }

        return null;
    }

}
