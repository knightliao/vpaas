package com.github.knightliao.vpaas.lc.server.server;

import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.netty.server.ILcServer;
import com.github.knightliao.vpaas.lc.server.server.dto.MqttRequest;
import com.github.knightliao.vpaas.lc.server.server.dto.ServerParam;

import io.netty.channel.ChannelFuture;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/5 21:11
 */
public interface IMyLcServer extends ILcServer {

    ServerParam getServerParam();

    ChannelFuture send(LcWrappedChannel channel, String topic, MqttRequest mqttRequest) throws InterruptedException;

}
