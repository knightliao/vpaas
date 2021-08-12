package com.github.knightliao.vpaas.lc.server.connect.netty.statistics.service;

import com.github.knightliao.vpaas.lc.server.connect.netty.statistics.service.impl.LcCounterServiceImpl;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/4 20:12
 */
public class LcCounterServiceFactory {

    public static ILcCounterService ILcCounterService = new LcCounterServiceImpl();

    public static ILcCounterService getILcCounterService() {
        return ILcCounterService;
    }
}
