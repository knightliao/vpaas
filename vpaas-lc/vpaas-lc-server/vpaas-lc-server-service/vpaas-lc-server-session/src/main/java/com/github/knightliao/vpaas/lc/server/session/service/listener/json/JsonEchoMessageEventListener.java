package com.github.knightliao.vpaas.lc.server.session.service.listener.json;

import com.github.knightliao.middle.lang.future.IInvokeFuture;
import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.netty.listener.LcMessageEventListener;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.RequestMsg;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.ResponseMsg;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.LcEventBehaviorEnum;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/7 17:53
 */
public class JsonEchoMessageEventListener implements LcMessageEventListener {

    @Override
    public LcEventBehaviorEnum channelRead(ChannelHandlerContext ctx, LcWrappedChannel channel, Object msg) {

        if (msg instanceof RequestMsg) {

            RequestMsg requestMsg = (RequestMsg) msg;

            if (requestMsg.getMessage() != null) {

                ResponseMsg responseMsg = new ResponseMsg();
                responseMsg.setSequence(requestMsg.getSequence());
                responseMsg.setCode(0);
                responseMsg.setResult(requestMsg.getMessage().toString());

                channel.writeAndFlush(responseMsg);
            }

        } else if (msg instanceof ResponseMsg) {

            ResponseMsg responseMsg = (ResponseMsg) msg;

            IInvokeFuture future = channel.getFutures().remove(responseMsg.getSequence());
            if (future != null) {
                if (responseMsg.getCause() != null) {
                    future.setCause(responseMsg.getCause());
                } else {
                    future.setResult(responseMsg);
                }
            }
        }

        return LcEventBehaviorEnum.CONTINUE;
    }
}
