package com.github.knightliao.vpaas.lc.server.start.support.dto;

import com.github.knightliao.vpaas.lc.server.connect.support.enums.SocketType;

import lombok.Data;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/7 17:13
 */
@Data
public class ServerOptionsDto {

    // 使用哪种协议
    private SocketType socketType = SocketType.MQTT;

    // broker name & id
    private String brokerName;
    private Integer brokerId;

    // 是否开启status端口
    private boolean openStatus = true;

    // 统计信息端口 默认 6001
    private int statusPort = 6001;

    // 绑定端口，默认 6000
    private int port = 6000;

    // 是否启用 keepAlive
    private boolean keepAlive = true;

    // 消息事件业务处理线程最小保持的线程数
    private int corePoolSize = 10;

    // 消息事件业务处理线程最大线程数
    private int maxmumPoolSize = 150;

    // 消息事件业务处理线程池队列最大值
    private int queueCapacity = 100 * 10000;

    // 工作线程
    // -1表示让机器自动控制
    private int bossCount = -1;
    private int workCount = 8 * 2;//8核
}
