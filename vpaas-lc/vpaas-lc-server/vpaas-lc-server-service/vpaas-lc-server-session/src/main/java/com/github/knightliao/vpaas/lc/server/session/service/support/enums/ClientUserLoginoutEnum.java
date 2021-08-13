package com.github.knightliao.vpaas.lc.server.session.service.support.enums;

import lombok.Getter;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/13 10:42
 */
@Getter
public enum ClientUserLoginoutEnum {

    CLIENT_LOGIN("CLIENT_LOGIN", 0),
    CLIENT_USER_LOGIN("CLIENT_USER_LOGIN", 1),
    USER_LOGIN("USER_LOGIN", 2),

    // 换用户登录
    USER_REPLACE_LOGIN("USER_REPLACE_LOGIN", 3),
    USER_LOGOUT("USER_LOGOUT", 4),

    // session中无用户，但还是继续登出
    USER_NO_USER_LOGOUT("USER_NO_USER_LOGOUT", 5),

    NULL("NULL", 10);

    private final String desc;
    private final int value;

    ClientUserLoginoutEnum(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public static ClientUserLoginoutEnum getByValue(Integer input) {
        for (ClientUserLoginoutEnum value : ClientUserLoginoutEnum.values()) {
            if (value.getValue() == input) {
                return value;
            }
        }

        return null;
    }
}
