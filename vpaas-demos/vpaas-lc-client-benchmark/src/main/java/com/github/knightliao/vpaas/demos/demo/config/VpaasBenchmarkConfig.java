package com.github.knightliao.vpaas.demos.demo.config;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/10/28 21:36
 */
@Slf4j
@Service
@Data
public class VpaasBenchmarkConfig {

    @Value("${vpaas.benchmark.serverList}")
    private String serverList;

    @Value("{vpaas.benchmark.concurrentConnectThreads:100}")
    private int concurrentConnectThreads;

    @Value("{vpaas.benchmark.pingtimeoutSecond:30}")
    private int pingtimeoutSecond;

    @Value("{vpaas.benchmark.loginPerhaps:100}")
    private int loginPerhaps;

    @Value("{vpaas.benchmark.logoutPerhaps:100}")
    private int logoutPerhaps;

    @Value("{vpaas.benchmark.disconnectPerhaps:100}")
    private int disconnectPerhaps;

    // 需要多少client实例，格式为 总大小:几台机器
    @Value("{vpaas.benchmark.multiClientsUids:10:1}")
    private String multiClientsUids;

    @Value("{vpaas.benchmark.usingMessNormalMode:1}")
    private int usingMessNormalMode;

    public ImmutablePair<Integer, Integer> getSizeInfo() {

        try {

            String[] ret = multiClientsUids.split(":");
            if (ret.length == 2) {
                int instanceSize = Integer.parseInt(ret[0]);
                int machineSize = Integer.parseInt(ret[1]);
                if (machineSize > 0 && instanceSize > 0) {
                    return new ImmutablePair<>(instanceSize, machineSize);
                }
            }
        } catch (Exception e) {

            log.error(e.toString(), e);
        }

        return ImmutablePair.of(0, 1);
    }
}
