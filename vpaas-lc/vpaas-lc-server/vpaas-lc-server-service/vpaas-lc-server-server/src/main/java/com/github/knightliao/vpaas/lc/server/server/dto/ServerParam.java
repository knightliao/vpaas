package com.github.knightliao.vpaas.lc.server.server.dto;

import lombok.Data;

/**
 * @author knightliao
 * @date 2021/8/5 21:02
 */
@Data
public class ServerParam {

    // 开始运行时间
    private long startTime = System.currentTimeMillis();

    // 统计信息端口，默认8001
    protected int statusPort = 8001;

    // 服务名称
    private String brokerName;
    private Integer brokerId;

    // 是否开启status端口
    protected boolean openStatus = true;
    private String webSocketPath = "/";
    private String mqttVersion = "server, mqttv3.1 mqttv3.1.1";
}
