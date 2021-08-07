package com.github.knightliao.vpaas.lc.server.connect.protocol.codec.json;

import java.util.List;

import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.BaseMessage;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * @author knightliao
 * @date 2021/8/7 10:34
 */
@ChannelHandler.Sharable
public class JsonEncoder extends MessageToMessageEncoder<BaseMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BaseMessage baseMessage, List<Object> list)
            throws Exception {

    }
}
