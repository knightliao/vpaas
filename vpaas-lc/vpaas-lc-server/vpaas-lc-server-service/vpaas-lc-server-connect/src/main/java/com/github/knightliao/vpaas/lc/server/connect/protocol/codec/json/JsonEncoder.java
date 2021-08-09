package com.github.knightliao.vpaas.lc.server.connect.protocol.codec.json;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.github.knightliao.vpaas.lc.server.connect.support.constants.ProtocolConstants;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.BaseMessage;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.LcHeartbeatMsg;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.RequestMsg;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.ResponseMsg;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * @author knightliao
 * @date 2021/8/7 10:34
 */
@ChannelHandler.Sharable
public class JsonEncoder extends MessageToMessageEncoder<BaseMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BaseMessage msg, List<Object> out)
            throws Exception {

        if (msg instanceof RequestMsg) {

            RequestMsg requestMsg = (RequestMsg) msg;

            JSONObject json = new JSONObject();
            json.put(ProtocolConstants.TYPE, JsonDecoder.REQUEST);
            json.put(ProtocolConstants.MESSAGE, requestMsg.getMessage());
            json.put(ProtocolConstants.SEQUENCE, requestMsg.getSequence());

            ByteBuf buf = Unpooled.copiedBuffer(json.toString().getBytes());
            out.add(buf);

        } else if (msg instanceof ResponseMsg) {

            ResponseMsg responseMsg = (ResponseMsg) msg;

            JSONObject json = new JSONObject();
            json.put(ProtocolConstants.TYPE, JsonDecoder.RESPONSE);
            json.put(ProtocolConstants.CODE, responseMsg.getCode());
            json.put(ProtocolConstants.RESULT, responseMsg.getResult());
            json.put(ProtocolConstants.SEQUENCE, responseMsg.getSequence());

            ByteBuf buf = Unpooled.copiedBuffer(json.toString().getBytes());
            out.add(buf);

        } else if (msg instanceof LcHeartbeatMsg) {

            JSONObject json = new JSONObject();
            json.put(ProtocolConstants.TYPE, JsonDecoder.HEARTBEAT);

            ByteBuf buf = Unpooled.copiedBuffer(json.toString().getBytes());
            out.add(buf);

        } else {

            throw new CodecException("unknown message type " + msg);
        }
    }
}
