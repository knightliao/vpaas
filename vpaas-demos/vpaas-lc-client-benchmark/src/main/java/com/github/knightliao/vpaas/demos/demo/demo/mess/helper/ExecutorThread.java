package com.github.knightliao.vpaas.demos.demo.demo.mess.helper;

import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.github.knightliao.middle.trace.MyTraceUtils;
import com.github.knightliao.vpaas.demos.demo.config.VpaasBenchmarkConfig;
import com.github.knightliao.vpaas.demos.demo.service.ClientSimulationAdvService;
import com.github.knightliao.vpaas.demos.demo.service.impl.ClientSimulationAdvServiceImpl;
import com.github.knightliao.vpaas.demos.demo.support.dto.ClientDemoContext;
import com.github.knightliao.vpaas.demos.demo.support.enums.ClientRunEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/10/28 21:44
 */
@Slf4j
public class ExecutorThread implements Runnable {

    private int shard = 1;
    private int start = 1;
    private int total = 1;

    private VpaasMultiDemoHelper vpaasMultiDemoHelper;
    private VpaasBenchmarkConfig vpaasBenchmarkConfig;

    private ConcurrentMap<String, ClientSimulationAdvService> map;
    private List<ClientSimulationAdvService> list;

    public ExecutorThread(int total, int start, int shard, VpaasMultiDemoHelper vpaasMultiDemoHelper,
                          VpaasBenchmarkConfig vpaasBenchmarkConfig, ConcurrentMap<String,
            ClientSimulationAdvService> map, List<ClientSimulationAdvService> list) {

        this.total = total;
        this.shard = shard;
        this.start = start;
        this.vpaasMultiDemoHelper = vpaasMultiDemoHelper;
        this.vpaasBenchmarkConfig = vpaasBenchmarkConfig;
        this.map = map;
        this.list = list;
    }

    @Override
    public void run() {

        try {

            for (int index = start; index < total; index += shard) {

                //
                MyTraceUtils.newReqId();

                try {

                    ImmutablePair<String, Long> clientUid = getClientAndUid(index);

                    //
                    ClientDemoContext clientDemoContext = ClientDemoContext.builder()
                            .clientId(clientUid.getLeft()).uid(clientUid.getRight())
                            .clientRunEnum(ClientRunEnum.MESS)
                            .keepAliveTimeoutSecond(vpaasBenchmarkConfig.getPingtimeoutSecond())
                            .logoutPerhaps(vpaasBenchmarkConfig.getLoginPerhaps())
                            .logoutPerhaps(vpaasBenchmarkConfig.getLogoutPerhaps())
                            .disconnectPerhaps(vpaasBenchmarkConfig.getDisconnectPerhaps())
                            .build();

                    //
                    ClientSimulationAdvService clientSimulationAdvService =
                            new ClientSimulationAdvServiceImpl(clientDemoContext);

                    SocketAddress socketAddress =
                            clientSimulationAdvService.newOne(vpaasBenchmarkConfig.getServerList());

                    map.put(clientUid.getLeft(), clientSimulationAdvService);
                    list.add(clientSimulationAdvService);

                    log.info("add " + socketAddress + " " + clientUid.getLeft() + " " + clientUid.getRight());

                } catch (Exception ex) {

                    log.error(ex.toString(), ex);

                }
            }
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }
    }

    private ImmutablePair<String, Long> getClientAndUid(int index) {

        while (true) {

            try {

                return vpaasMultiDemoHelper.getClientAndUid(index);

            } catch (Exception ex) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
            }
        }
    }
}
