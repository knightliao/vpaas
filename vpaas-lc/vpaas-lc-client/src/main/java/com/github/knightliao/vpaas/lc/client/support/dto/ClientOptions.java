package com.github.knightliao.vpaas.lc.client.support.dto;

import com.github.knightliao.vpaas.lc.server.connect.support.enums.SocketType;

import lombok.Data;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/8 23:24
 */
@Data
public class ClientOptions {

    private SocketType socketType = SocketType.MQTT;

    // 格式为 ip:port,ip:port
    private String serverList;

    private int connectTimeout = 3000;

    private String clientName;
}
