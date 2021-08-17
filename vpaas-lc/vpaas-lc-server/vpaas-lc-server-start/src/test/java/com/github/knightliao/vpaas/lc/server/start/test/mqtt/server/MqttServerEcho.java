package com.github.knightliao.vpaas.lc.server.start.test.mqtt.server;

import com.alibaba.fastjson.JSONObject;
import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.server.IMyLcServer;
import com.github.knightliao.vpaas.lc.server.server.dto.MqttRequest;
import com.github.knightliao.vpaas.lc.server.session.service.listener.mqtt.MqttEchoMessageEventListener;
import com.github.knightliao.vpaas.lc.server.start.factory.SimpleNewServerFactory;
import com.github.knightliao.vpaas.lc.server.start.support.dto.ServerOptionsDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/17 14:14
 */
@Slf4j
public class MqttServerEcho {

    public static void main(String[] args) throws Exception {

        ServerOptionsDto serverOptionsDto = new ServerOptionsDto();
        serverOptionsDto.setPort(7000);

        IMyLcServer server = SimpleNewServerFactory.newServer(0);
        server.addEventListener(new MqttEchoMessageEventListener());
        server.bind();

        // 模拟发送
        JSONObject message = new JSONObject();
        message.put("action", "echo");

        MqttRequest mqttRequest = new MqttRequest(message.toString().getBytes());
        int index = 0;
        while (true) {

            if (server.getChannels().size() > 0) {

                //log.info("do push " + index);
                for (LcWrappedChannel channel : server.getChannels().values()) {
                    log.info(channel.id() + " send");
                    server.send(channel, "server/", mqttRequest);
                }
            }

            index++;

            Thread.sleep(1000L);
        }
    }
}
