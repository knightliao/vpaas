package com.github.knightliao.vpaas.lc.server.connect.netty.statistics.dto;

import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;

/**
 * 一些核心统计数据
 *
 * @author knightliao
 * @date 2021/8/4 20:18
 */
@Getter
public class LcCountInfoDto {

    // 最后接收消息
    private long lastReceiveTimeStamp;

    // 最后接收消息
    private long lastSentTimeStamp;

    // 最大连接数
    private long maxChannelNum;

    //
    private long maxChannelGroupNum;

    // 接收消息数
    private AtomicLong receiveNum = new AtomicLong();

    // 发送消息数
    private AtomicLong sentNum = new AtomicLong();

    // 收发心跳数
    private AtomicLong heartbeatNum = new AtomicLong();

    public void setMaxChannelNum(long curChannelNum) {
        if (this.maxChannelNum < curChannelNum) {
            this.maxChannelNum = curChannelNum;
        }
    }

    public void setLastReceiveTimeStamp(long lastReceiveTimeStamp) {
        if (this.lastReceiveTimeStamp < lastReceiveTimeStamp) {
            this.lastReceiveTimeStamp = lastReceiveTimeStamp;
        }
    }

    public void setLastSentTimeStamp(long lastSentTimeStamp) {
        if (this.lastSentTimeStamp < lastSentTimeStamp) {
            this.lastSentTimeStamp = lastSentTimeStamp;
        }
    }
}
