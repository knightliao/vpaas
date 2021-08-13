package com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;

import com.github.knightliao.vpaas.lc.server.connect.netty.server.LcServerContext;
import com.github.knightliao.vpaas.lc.server.session.service.protocol.IProtocolProcessor;
import com.github.knightliao.vpaas.lc.server.session.service.support.utils.MqttLogUtils;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/13 17:09
 */
@Service
@Slf4j
public class MqttProtocolProcess {

    @Resource
    private IProtocolProcessor connect;

    private Map<MqttMessageType, IProtocolProcessor> protocolProcessMap = new HashMap<>();

    @PostConstruct
    public void init() {

        protocolProcessMap.put(MqttMessageType.CONNECT, connect);
    }

    public void process(MqttMessageType mqttMessageType, Channel channel, Object msg) throws Throwable {

        if (!protocolProcessMap.containsKey(mqttMessageType)) {

            log.error("cannot find processor for " + mqttMessageType.toString());
            return;
        }

        boolean isSuccess = true;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {

            IProtocolProcessor protocolProcessor = protocolProcessMap.get(mqttMessageType);

            if (!protocolProcessor.canGo(msg)) {
                log.error("type not ok " + msg.toString());
                return;
            }

            protocolProcessor.doPre(channel, msg);
            protocolProcessor.process(channel, msg);
            protocolProcessor.doAfter(channel, msg);

        } catch (Throwable throwable) {

            isSuccess = false;
            throw new Throwable(throwable);

        } finally {

            stopWatch.stop();
            long costTime = stopWatch.getTime();
            long brokerId = LcServerContext.getContext().getBrokerId();

            MqttLogUtils.doSessionLog(mqttMessageType, costTime, isSuccess, msg);
        }

    }
}
