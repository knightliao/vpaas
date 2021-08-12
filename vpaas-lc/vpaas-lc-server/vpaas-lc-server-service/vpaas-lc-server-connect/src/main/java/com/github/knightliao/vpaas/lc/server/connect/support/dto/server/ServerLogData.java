package com.github.knightliao.vpaas.lc.server.connect.support.dto.server;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.experimental.Builder;
import lombok.experimental.Tolerate;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/4 22:50
 */
@Data
@Builder
public class ServerLogData {

    private Set<String> logClients;
    private Set<Long> logUids;
    private int logAllOpen;

    @Tolerate
    public ServerLogData() {

        logClients = new HashSet<>();
        logUids = new HashSet<>();
    }
}
