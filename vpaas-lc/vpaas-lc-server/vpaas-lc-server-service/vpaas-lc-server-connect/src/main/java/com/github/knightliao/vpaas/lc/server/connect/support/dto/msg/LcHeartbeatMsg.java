package com.github.knightliao.vpaas.lc.server.connect.support.dto.msg;

/**
 * @author knightliao
 * @date 2021/8/4 22:43
 */
public class LcHeartbeatMsg extends BaseMessage {

    public static final byte[] BYTES = new byte[0];

    private static LcHeartbeatMsg instance = new LcHeartbeatMsg();

    public static LcHeartbeatMsg getSingleton() {
        return instance;
    }

    private LcHeartbeatMsg() {
    }
}
