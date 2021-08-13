package com.github.knightliao.vpaas.lc.server.server.service.impl.helper;

import java.util.LinkedHashMap;

import com.github.knightliao.vpaas.lc.server.connect.dispatch.dispatcher.ILcEventDispatcher;
import com.github.knightliao.vpaas.lc.server.connect.netty.handler.LcCountHandler;
import com.github.knightliao.vpaas.lc.server.connect.netty.handler.ServerDispatchHandler;
import com.github.knightliao.vpaas.lc.server.connect.netty.handler.ServerHeartbeatHandler;
import com.github.knightliao.vpaas.lc.server.connect.netty.server.ILcServer;
import com.github.knightliao.vpaas.lc.server.connect.netty.service.ILcService;
import com.github.knightliao.vpaas.lc.server.connect.netty.service.LcService;
import com.github.knightliao.vpaas.lc.server.connect.protocol.codec.json.JsonDecoder;
import com.github.knightliao.vpaas.lc.server.connect.protocol.codec.json.JsonEncoder;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.param.LcServiceParam;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.SocketType;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/6 14:09
 */
public class ServerPipeline {

    private final static String IDLE_HANDLER_NAME = "idle";

    public static ChannelHandler getChildChannelHandler(LcService lcService, ILcEventDispatcher lcEventDispatcher,
                                                        LcCountHandler lcCountHandler) {

        return new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {

                ChannelPipeline pipeline = socketChannel.pipeline();

                // 注册各种自定义handler
                LinkedHashMap<String, ChannelHandler> handlers = lcService.getHandlers();
                for (String key : handlers.keySet()) {
                    pipeline.addLast(key, handlers.get(key));
                }

                //
                LcServiceParam lcServiceParam = lcService.getLcServiceParam();

                // heartbeat
                if (lcServiceParam.isCheckHeartbeat()) {
                    pipeline.addLast(IDLE_HANDLER_NAME, getIdleStateHandler(lcServiceParam));
                    pipeline.addLast("heartbeatHandler", getHeartbeatHandler());
                }

                // socket type
                addPipelineForSocketType(lcServiceParam, pipeline);

                // 注册事件分发handler
                ServerDispatchHandler dispatchHandler = new ServerDispatchHandler(lcEventDispatcher,
                        (ILcServer) lcService);
                pipeline.addLast("dispatcherHandler", dispatchHandler);

                // 注册server统计信息handler
                if (lcServiceParam.isOpenCount()) {
                    pipeline.addLast("counterHandler", lcCountHandler);
                }
            }
        };
    }

    public static ChannelHandler getStatusChildChannelHandler(ILcService lcService,
                                                              ILcEventDispatcher lcEventDispatcher) {

        return new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {

                LinkedHashMap<String, ChannelHandler> handlers = lcService.getHandlers();

                ChannelPipeline pipeline = socketChannel.pipeline();

                // 由于DelimiterBasedFrameDecoder不是 sharable ，因为每次都要new
                pipeline.addLast("framer", new DelimiterBasedFrameDecoder(1024, Delimiters.lineDelimiter()));

                // 注册各种自定义handler
                for (String key : handlers.keySet()) {
                    pipeline.addLast(key, handlers.get(key));
                }

                // 注册事件分发handler
                ServerDispatchHandler dispatchHandler = new ServerDispatchHandler(lcEventDispatcher,
                        (ILcServer) lcService);
                pipeline.addLast("dispatcherHandler", dispatchHandler);
            }
        };
    }

    private static void addPipelineForSocketType(LcServiceParam lcServiceParam, ChannelPipeline pipeline) {

        SocketType socketType = lcServiceParam.getSocketType();

        if (socketType.equals(SocketType.NORMAL)) {

        } else if (socketType.equals(SocketType.JSON)) {

            pipeline.addLast("decoder", new JsonDecoder());
            pipeline.addLast("encoder", new JsonEncoder());

        } else if (socketType.equals(SocketType.MQTT)) {

            //

        } else if (socketType.equals(SocketType.MQTT_WS)) {

            //

        }
    }

    private static IdleStateHandler getIdleStateHandler(LcServiceParam lcServiceParam) {

        return new IdleStateHandler(lcServiceParam.getReadIdleTimeSeconds(), lcServiceParam.getWriteIdleTimeSeconds(),
                lcServiceParam.getAllIdleTimeSeconds());
    }

    private static ChannelInboundHandlerAdapter getHeartbeatHandler() {

        return new ServerHeartbeatHandler();
    }

    public static void replaceIdleHandler(Channel channel, int keepSeconds) {

        if (channel.pipeline().names().contains(IDLE_HANDLER_NAME)) {
            channel.pipeline().remove(IDLE_HANDLER_NAME);
        }

        channel.pipeline().addFirst(IDLE_HANDLER_NAME,
                new IdleStateHandler(0, 0, keepSeconds));
    }
}
