package com.github.knightliao.vpaas.demos.demo.support.dto;

import com.github.knightliao.vpaas.demos.demo.support.enums.ClientRunEnum;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/29 01:42
 */
@Data
@Builder
public class ClientDemoContext {

    private Long uid;
    private String clientId;

    private ClientRunEnum clientRunEnum;
    private int keepAliveTimeoutSecond;

    private int loginPerhaps;
    private int logoutPerhaps;
    private int disconnectPerhaps;

    @Tolerate
    public ClientDemoContext() {

    }
}
