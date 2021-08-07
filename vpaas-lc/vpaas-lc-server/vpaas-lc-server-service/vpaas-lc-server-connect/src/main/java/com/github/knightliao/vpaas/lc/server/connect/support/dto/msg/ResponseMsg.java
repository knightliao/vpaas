package com.github.knightliao.vpaas.lc.server.connect.support.dto.msg;

import lombok.Data;
import lombok.ToString;

/**
 * @author knightliao
 * @date 2021/8/7 18:03
 */
@Data
@ToString(callSuper = true)
public class ResponseMsg extends BaseMessage {

    public static final int EXCEPTION = -1;

    public static final int SUCCESS = 0;

    private int code;
    private Object result;
    private Throwable cause;
}
