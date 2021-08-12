package com.github.knightliao.vpaas.lc.client.support.dto;

import lombok.Data;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/8 17:50
 */
@Data
public class LcClientParam {

    private String webSocketPath = "/";

    private String mqttVersion = "server, mqttv3.1 mqttv3.1.1";

    // 建立连接超时的时间（毫秒），默认 3000
    protected int connectTimeout = 3000;

    // 同步调用默认超时时间
    protected int syncInvokeTimeout = 3000;
}
