package com.github.knightliao.vpaas.lc.server.session.service.store.dto;

import lombok.Data;
import lombok.experimental.Builder;
import lombok.experimental.Tolerate;

/**
 * @author knightliao
 * @date 2021/8/11 14:50
 */
@Data
@Builder
public class SessionStoreDto {

    // 业务id
    private String groupKey;

    // brokerid
    private Integer brokerId;

    // 服务器ip
    private String serverIp;

    // 服务器port
    private int serverPort;

    // 客户端ip
    private String clientIp;

    // 登录uid
    private long uid;

    // 通道id
    private String channelLocalId;

    // 超时时间
    private int expireSeconds;

    // 会话生成时间
    private long createTs;

    // 最后一次connect 更新时间
    private long lastConnectTs;

    // ping 时的时间
    private long pingTimestamp;

    // 客户端版本
    private String clientVer;

    // 加密类型
    private String signType;

    //
    private int connectType;

    //
    private int loginLogoutType;

    //
    private long quickLoginTimes;

    @Tolerate
    public SessionStoreDto() {

    }
}