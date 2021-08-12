package com.github.knightliao.vpaas.lc.server.connect.protocol.codec.json;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.knightliao.vpaas.lc.server.connect.support.constants.ProtocolConstants;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.LcHeartbeatMsg;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.RequestMsg;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.ResponseMsg;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/7 10:34
 */
@ChannelHandler.Sharable
public class JsonDecoder extends MessageToMessageDecoder<ByteBuf> {

    protected static final String REQUEST = "request";
    protected static final String RESPONSE = "response";
    protected static final String HEARTBEAT = "heartbeat";

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out)
            throws Exception {

        try {

            byte[] tmp = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(tmp);
            String jsonStr = new String(tmp);

            JSONObject json = JSON.parseObject(jsonStr);
            String type = json.getString(ProtocolConstants.TYPE);

            if (REQUEST.equalsIgnoreCase(type)) {
                RequestMsg requestMsg = new RequestMsg();
                requestMsg.setSequence(json.getLong(ProtocolConstants.SEQUENCE));
                requestMsg.setMessage(json.getString(ProtocolConstants.MESSAGE));
                out.add(requestMsg);

            } else if (RESPONSE.equalsIgnoreCase(type)) {

                ResponseMsg responseMsg = new ResponseMsg();
                responseMsg.setSequence(json.getLong(ProtocolConstants.SEQUENCE));
                responseMsg.setCode(json.getIntValue(ProtocolConstants.CODE));
                responseMsg.setResult(json.get(ProtocolConstants.RESULT));
                out.add(responseMsg);

            } else if (HEARTBEAT.equalsIgnoreCase(type)) {

                out.add(LcHeartbeatMsg.getSingleton());
            }

        } catch (Exception ex) {
            throw new CodecException(ex);
        }

    }
}
