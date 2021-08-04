package com.github.knightliao.vpaas.lc.server.connect.support.dto.param;

import com.github.knightliao.vpaas.lc.server.connect.netty.statistics.dto.LcCountInfoDto;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.SocketType;

import lombok.Data;

/**
 * @author knightliao
 * @date 2021/8/4 20:49
 */
@Data
public class LcServiceParam {

    // socket类型
    private SocketType socketType = SocketType.NORMAL;

    // 绑定端口，默认为8000
    private int port = 8000;

    // 多ip情况下绑定指定ip(可以不设置）
    private String ip;

    // 是否启动keepalive
    private boolean keepAlive = true;

    // 是否启动tcpnodelay
    private boolean tcpNoDelay = true;

    // 工作线程大小
    private int workerCount;
    private int bossCount;

    // 是否开启处理线程池
    private boolean openExecutor = false;

    // 消息事件业务处理线程池最小保持的线程数
    private int corePoolSize = 10;
    // 消息事件业务处理线程池最大线程数
    private int maximumPoolSize = 150;
    // 消息事件业务处理线程池队列最大值
    private int queueCapacity = 100 * 10000;

    // channel 事件业务处理线程池最小保持的线程数
    private int corePoolChannelSize = 5;
    // channel 事件业务处理线程池最大线程数
    private int maximumPoolChannelSize = 75;
    // channel 事件业务处理线程池最大线程数
    private int queueChannelCapacity = 100 * 10000;

    // exception 事件业务处理线程池最小保持的线程数
    private int corePoolExceptionSize = 5;
    // exception 事件业务处理线程池最大线程数
    private int maximumPoolExceptionSize = 75;
    // exception 事件业务处理线程池最大线程数
    private int queueExceptionCapacity = 100 * 10000;

    // 设置心跳就检查
    private boolean checkHeartbeat = true;

    // 心跳检查时的读闲时时间
    private int readIdleTimeSeconds = 0;
    // 心跳检查时的写闲时时间
    private int writeIdleTimeSeconds = 0;
    // 心跳检查时的读写空闲时间
    private int allIdleTimeSeconds = 60;

    // server统计信息
    protected LcCountInfoDto countInfoDto = new LcCountInfoDto();
    // 是否开启统计信息
    protected boolean openCount = true;
    // 是否需要用户名和密码
    protected boolean passwordMust = true;
}
