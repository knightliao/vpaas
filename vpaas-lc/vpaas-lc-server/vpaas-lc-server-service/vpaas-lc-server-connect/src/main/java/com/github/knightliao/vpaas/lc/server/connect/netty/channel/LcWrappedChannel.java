package com.github.knightliao.vpaas.lc.server.connect.netty.channel;

import java.nio.channels.ClosedChannelException;

import com.github.knightliao.vpaas.common.utils.async.future.IInvokeFuture;
import com.github.knightliao.vpaas.lc.server.connect.netty.statistics.service.LcCounterServiceFactory;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.LcHeartbeatMsg;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;

/**
 * 重写channel
 *
 * @author knightliao
 * @date 2021/8/4 15:13
 */
public class LcWrappedChannel extends LcWrappedBaseChannel {

    protected final ChannelFutureListener sendSuccessListener = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
            if (channelFuture.isSuccess()) {

            }
        }
    };

    public LcWrappedChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("channel cannot be null");
        }
        this.channel = channel;
    }

    @Override
    public ChannelFuture write(Object o) {

        boolean ret = LcCounterServiceFactory.getILcCounterService().isCountStatistic();
        return this.write(o, ret);
    }

    @Override
    public ChannelFuture write(Object o, ChannelPromise channelPromise) {
        boolean ret = LcCounterServiceFactory.getILcCounterService().isCountStatistic();
        return this.write(o, channelPromise, ret);
    }

    private ChannelFuture write(Object message, boolean isStatistic) {

        ChannelFuture future = channel.write(message);

        if (isStatistic) {
            if (message instanceof LcHeartbeatMsg) {
            } else {
                future.addListener(sendSuccessListener);
            }
        }

        return future;
    }

    private ChannelFuture write(Object message, ChannelPromise channelPromise, boolean isStatistic) {

        ChannelFuture future = channel.write(message, channelPromise);

        if (isStatistic) {
            if (message instanceof LcHeartbeatMsg) {
            } else {
                future.addListener(sendSuccessListener);
            }
        }

        return future;
    }

    @Override
    public ChannelFuture writeAndFlush(Object o, ChannelPromise channelPromise) {

        boolean ret = LcCounterServiceFactory.getILcCounterService().isCountStatistic();
        return this.writeAndFlush(o, channelPromise, ret);
    }

    @Override
    public ChannelFuture writeAndFlush(Object o) {

        boolean ret = LcCounterServiceFactory.getILcCounterService().isCountStatistic();
        return this.writeAndFlush(o, ret);
    }

    private ChannelFuture writeAndFlush(Object message, ChannelPromise channelPromise, boolean isStatistic) {

        ChannelFuture future = channel.writeAndFlush(message, channelPromise);

        if (isStatistic) {
            if (message instanceof LcHeartbeatMsg) {
            } else {
                future.addListener(sendSuccessListener);
            }
        }

        return future;
    }

    public ChannelFuture writeAndFlush(Object message, boolean isStatistic) {

        ChannelFuture future = channel.writeAndFlush(message);

        if (isStatistic) {
            if (message instanceof LcHeartbeatMsg) {
            } else {
                future.addListener(sendSuccessListener);
            }
        }

        return future;
    }

    @Override
    public ChannelFuture close() {
        return innerClose();
    }

    private ChannelFuture innerClose() {

        ChannelFuture channelFuture = channel.close();

        // cancel 所有等待中的 invoker future
        for (IInvokeFuture invokeFuture : futures.values()) {
            if (!invokeFuture.isDone()) {
                invokeFuture.setCause(new ClosedChannelException());
            }
        }

        return channelFuture;
    }
}
