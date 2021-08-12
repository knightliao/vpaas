package com.github.knightliao.vpaas.lc.server.connect.dispatch.dispatcher;

import com.github.knightliao.vpaas.lc.server.connect.dispatch.dispatcher.impl.LcEventDispatcherImpl;
import com.github.knightliao.vpaas.lc.server.connect.netty.service.LcService;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/4 23:14
 */
public class LcEventDispatcherFactory {

    public static ILcEventDispatcher getEventDispatcherDefaultImpl(LcService lcService) {

        return new LcEventDispatcherImpl(lcService);
    }
}
