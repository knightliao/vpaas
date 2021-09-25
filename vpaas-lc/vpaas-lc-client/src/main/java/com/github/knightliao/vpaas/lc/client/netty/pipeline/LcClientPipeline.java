package com.github.knightliao.vpaas.lc.client.netty.pipeline;

import java.util.LinkedHashMap;

import com.github.knightliao.vpaas.lc.client.netty.handler.ClientDispatchHandler;
import com.github.knightliao.vpaas.lc.client.support.dto.LcClientParam;
import com.github.knightliao.vpaas.lc.server.connect.dispatch.dispatcher.ILcEventDispatcher;
import com.github.knightliao.vpaas.lc.server.connect.netty.handler.ServerHeartbeatHandler;
import com.github.knightliao.vpaas.lc.server.connect.netty.service.ILcService;
import com.github.knightliao.vpaas.lc.server.connect.protocol.codec.json.JsonDecoder;
import com.github.knightliao.vpaas.lc.server.connect.protocol.codec.json.JsonEncoder;
import com.github.knightliao.vpaas.lc.server.connect.protocol.codec.mqtt.MqttDecoder;
import com.github.knightliao.vpaas.lc.server.connect.protocol.codec.mqtt.MyMqttEncoder;
import com.github.knightliao.vpaas.lc.server.connect.protocol.codec.mqttwc.MqttWebSocketCodec;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.param.LcServiceParam;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.SocketType;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/8 22:48
 */
public class LcClientPipeline {

    public static ChannelHandler getChildChannelHandler(ILcService lcService,
                                                        ILcEventDispatcher eventDispatcher,
                                                        LcClientParam lcClientParam) {

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

                // system
                if (lcServiceParam.isCheckHeartbeat()) {
                    pipeline.addLast("timeoutHandler", getIdleStateHandler(lcServiceParam));
                    pipeline.addLast("heartbeatHandler", getHeartbeatHandler());
                }

                // socket type
                SocketType socketType = lcServiceParam.getSocketType();

                if (socketType.equals(SocketType.NORMAL)) {

                } else if (socketType.equals(SocketType.JSON)) {

                    pipeline.addLast("decoder", new JsonDecoder());
                    pipeline.addLast("encoder", new JsonEncoder());

                } else if (socketType.equals(SocketType.MQTT)) {
                    //
                    pipeline.addLast("myMqttDecoder", new MqttDecoder());
                    pipeline.addLast("myMqttEncoder", MyMqttEncoder.INSTANCE);

                } else if (socketType.equals(SocketType.MQTT_WS)) {

                    //
                    pipeline.addLast("httpServerCodec", new HttpServerCodec());
                    pipeline.addLast("httpObjectAggregator", new HttpObjectAggregator(65536));
                    pipeline.addLast("mqttWebSocketCodec", new MqttWebSocketCodec());
                    pipeline.addLast("mqttDecoder", new MqttDecoder());
                    pipeline.addLast("myMqttEncoder", MyMqttEncoder.INSTANCE);

                    WebSocketServerProtocolHandler webSocketServerProtocolHandler =
                            new WebSocketServerProtocolHandler(lcClientParam.getWebSocketPath(),
                                    lcClientParam.getMqttVersion());
                    pipeline.addLast("webSocketHandler", webSocketServerProtocolHandler);
                }

                // 注册事件分发handler
                ClientDispatchHandler clientDispatchHandler = new ClientDispatchHandler(eventDispatcher, lcService);
                pipeline.addLast("dispatchHandler", clientDispatchHandler);
            }
        };

    }

    private static IdleStateHandler getIdleStateHandler(LcServiceParam lcServiceParam) {

        return new IdleStateHandler(lcServiceParam.getReadIdleTimeSeconds(), lcServiceParam.getWriteIdleTimeSeconds(),
                lcServiceParam.getAllIdleTimeSeconds());
    }

    private static ChannelInboundHandlerAdapter getHeartbeatHandler() {

        return new ServerHeartbeatHandler();
    }

}
