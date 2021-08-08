package com.github.knightliao.vpaas.lc.client.netty.channel;

import java.util.concurrent.TimeUnit;

import com.github.knightliao.vpaas.common.utils.async.future.IInvokeFuture;
import com.github.knightliao.vpaas.common.utils.async.future.InvokeFutureFactory;
import com.github.knightliao.vpaas.common.utils.exceptions.SocketRuntimeException;
import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.RequestMsg;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.ResponseMsg;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * 重写channel
 *
 * @author knightliao
 * @date 2021/8/4 15:13
 */
public class LcWrappedClientChannel extends LcWrappedChannel {

    public LcWrappedClientChannel(Channel channel) {
        super(channel);
    }

    public ChannelFuture send(Object msg) {
        return this.writeAndFlush(msg);
    }

    public ResponseMsg sendSync(final RequestMsg message, int timeout) {

        final IInvokeFuture invokeFuture = InvokeFutureFactory.getInvokeFutureDefaultImpl();

        try {

            //
            futures.put(message.getSequence(), invokeFuture);

            // 发送request对象
            ChannelFuture channelFuture = writeAndFlush(message);

            // 监听结束
            channelFuture.addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {

                    if (!channelFuture.isSuccess()) {
                        futures.remove(message.getSequence());
                        invokeFuture.setCause(channelFuture.cause());
                    }
                }
            });

        } catch (Throwable throwable) {

            throw new SocketRuntimeException(throwable);
        }

        // 等级结果
        Object retObj;
        if (timeout > 0) {
            retObj = invokeFuture.getResult(timeout, TimeUnit.MILLISECONDS);
        } else {
            //
            retObj = invokeFuture.getResult();
        }

        return (ResponseMsg) retObj;
    }

}
