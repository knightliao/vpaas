package com.github.knightliao.vpaas.lc.server.start.factory;

import javax.annotation.Resource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import com.github.knightliao.middle.log.LoggerUtil;
import com.github.knightliao.vpaas.common.rely.config.bean.VpaasConfig;
import com.github.knightliao.vpaas.lc.server.server.IMyLcServer;
import com.github.knightliao.vpaas.lc.server.session.service.listener.mqtt.VpaasMqttMessageEventListener;

import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/13 17:31
 */
@Slf4j
@Service
public class ServerMain implements ApplicationRunner {

    @Resource
    private VpaasMqttMessageEventListener vpaasMqttMessageEventListener;

    @Resource
    private VpaasConfig vpaasConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        //
        IMyLcServer server =
                SimpleNewServerFactory.newServer(
                        vpaasConfig.getServerBrokerId(),
                        vpaasConfig.getServerPort(),
                        vpaasConfig.getServerStatusPort());

        // add listener
        server.addEventListener(vpaasMqttMessageEventListener);

        //
        ChannelFuture channelFuture = server.bind();
        channelFuture.awaitUninterruptibly();

        //
        LoggerUtil.info(log, "ServerMain start");

    }
}
