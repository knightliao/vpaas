package com.github.knightliao.vpaas.lc.server.connect.support.enums;

import lombok.Getter;

/**
 * @author knightliao
 * @date 2021/8/4 20:50
 */
@Getter
public enum SocketType {

    NORMAL("MESSAGE", 0),
    JSON("JSON", 1),
    MQTT("MQTT", 2),
    MQTT_WS("MQTT_WS", 3);

    private final String desc;
    private final int value;

    SocketType(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public static SocketType getByValue(Integer input) {
        for (SocketType value : SocketType.values()) {
            if (value.getValue() == input) {
                return value;
            }
        }

        return null;
    }
}
