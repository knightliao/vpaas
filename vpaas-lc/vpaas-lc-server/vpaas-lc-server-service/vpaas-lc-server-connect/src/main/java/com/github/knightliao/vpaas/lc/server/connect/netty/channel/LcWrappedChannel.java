package com.github.knightliao.vpaas.lc.server.connect.netty.channel;

import java.io.IOException;
import java.nio.channels.Channel;
import java.util.concurrent.ConcurrentHashMap;

import com.github.knightliao.vpaas.common.utils.async.future.IInvokeFuture;

/**
 *
 * 重写channel
 *
 * @author knightliao
 * @date 2021/8/4 15:13
 */
public class LcWrappedChannel implements Channel {

    // 原指针
    protected Channel channel;

    protected ConcurrentHashMap<Long, IInvokeFuture> map = new ConcurrentHashMap<>();

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void close() throws IOException {

    }
}
