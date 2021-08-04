package com.github.knightliao.vpaas.lc.server.connect.netty.statistics.service;

/**
 * 长连接统计类
 *
 * @author knightliao
 * @date 2021/8/4 20:10
 */
public interface ILcCounterService {

    void incrSend();

    void incrReceive();

    void setMaxChannelNum(long channelNum);

    void incrHeartbeatNum();

    boolean isCountStatistic();
}
