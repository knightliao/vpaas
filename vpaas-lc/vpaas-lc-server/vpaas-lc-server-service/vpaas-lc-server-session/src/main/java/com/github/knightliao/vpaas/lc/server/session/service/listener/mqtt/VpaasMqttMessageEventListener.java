package com.github.knightliao.vpaas.lc.server.session.service.listener.mqtt;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.knightliao.middle.log.LoggerUtil;
import com.github.knightliao.middle.thread.MyThreadContext;
import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.netty.listener.LcChannelEventListener;
import com.github.knightliao.vpaas.lc.server.connect.netty.listener.LcExceptionEventListener;
import com.github.knightliao.vpaas.lc.server.connect.netty.listener.LcMessageEventListener;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.LcEventBehaviorEnum;
import com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.MqttProtocolProcess;
import com.github.knightliao.vpaas.lc.server.session.service.support.helper.ServerLogHelper;
import com.github.knightliao.vpaas.lc.server.session.service.support.helper.VpaasSessionDisconnectHelper;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/10 16:09
 */
@Slf4j
@Service
public class VpaasMqttMessageEventListener implements LcMessageEventListener, LcChannelEventListener,
        LcExceptionEventListener {

    @Resource
    private MqttProtocolProcess mqttProtocolProcess;

    @Resource
    private VpaasSessionDisconnectHelper vpaasSessionDisconnectHelper;

    @Resource
    private ServerLogHelper serverLogHelper;

    @Override
    public LcEventBehaviorEnum channelActive(ChannelHandlerContext ctx, LcWrappedChannel channel) {
        return LcEventBehaviorEnum.CONTINUE;
    }

    @Override
    public LcEventBehaviorEnum channelInactive(ChannelHandlerContext ttx, LcWrappedChannel channel) {

        vpaasSessionDisconnectHelper.disconnect(channel);
        return LcEventBehaviorEnum.CONTINUE;
    }

    @Override
    public LcEventBehaviorEnum exceptionCaught(ChannelHandlerContext ctx, LcWrappedChannel channel,
                                               Throwable throwable) {
        if (channel != null) {
            channel.close();
        }

        return LcEventBehaviorEnum.BREAK;
    }

    @Override
    public LcEventBehaviorEnum channelRead(ChannelHandlerContext ctx, LcWrappedChannel channel, Object msg) {

        //
        if (channel == null) {
            return LcEventBehaviorEnum.BREAK;
        }

        try {

            //
            MyThreadContext.init();
            // 在这个线程的执行过程中，全部统一进行控制
            serverLogHelper.setUpForLog(channel);

            if (msg instanceof MqttMessage) {

                MqttMessage mqttMessage = (MqttMessage) msg;

                // 校验格式，失败就断开连接
                boolean checkRet = checkMsg(mqttMessage, channel);
                if (!checkRet) {
                    return LcEventBehaviorEnum.BREAK;
                }

                //
                MqttMessageType mqttMessageType = mqttMessage.fixedHeader().messageType();

                switch (mqttMessageType) {
                    case CONNECT:
                        mqttProtocolProcess.process(MqttMessageType.CONNECT, channel, msg);
                        break;

                    case PUBLISH:
                        mqttProtocolProcess.process(MqttMessageType.PUBLISH, channel, msg);
                        break;

                    case PUBACK:
                        mqttProtocolProcess.process(MqttMessageType.PUBACK, channel, msg);
                        break;

                    case SUBSCRIBE:
                        break;

                    case UNSUBSCRIBE:
                        break;

                    case PINGREQ:
                        mqttProtocolProcess.process(MqttMessageType.PINGREQ, channel, msg);
                        break;

                    case PINGRESP:
                        mqttProtocolProcess.process(MqttMessageType.PINGRESP, channel, msg);
                        break;

                    default:
                        LoggerUtil.error(log, "Nonsupport server message type of {0}", mqttMessage);
                }
            }

            return LcEventBehaviorEnum.CONTINUE;

        } catch (Throwable throwable) {

            throw new RuntimeException(throwable);

        } finally {

            MyThreadContext.clean();
        }

    }

    private boolean checkMsg(MqttMessage mqttMessage, Channel channel) {

        boolean ret = true;
        if (mqttMessage == null || mqttMessage.fixedHeader() == null) {
            ret = false;
        }

        if (!ret) {
            if (channel != null) {
                channel.close();
            }
        }

        return ret;
    }
}
