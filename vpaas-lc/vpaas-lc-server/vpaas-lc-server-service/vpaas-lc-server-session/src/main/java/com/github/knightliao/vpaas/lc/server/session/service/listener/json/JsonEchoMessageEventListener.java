package com.github.knightliao.vpaas.lc.server.session.service.listener.json;

import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.netty.listener.LcMessageEventListener;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.RequestMsg;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.ResponseMsg;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.LcEventBehaviorEnum;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author knightliao
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
        }

        return LcEventBehaviorEnum.CONTINUE;
    }
}
