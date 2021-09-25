package com.github.knightliao.vpaas.lc.server.session.service.support.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.knightliao.middle.log.LoggerUtil;
import com.github.knightliao.middle.metrics.MonitorHelper;
import com.github.knightliao.vpaas.lc.server.common.common.constants.VpaasServerConstants;
import com.github.knightliao.vpaas.lc.server.connect.support.utils.LogNeedUtils;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/13 11:42
 */
@Slf4j
public class SessionLogUtils {

    private static Logger LOGGER_MQTT_SESSION_OP_LOG =
            LoggerFactory.getLogger((VpaasServerConstants.LOGGER_MQTT_SESSION_OP_LOG));
    private static Logger LOGGER_MQTT_IDLE_TIMEOUT_LOG =
            LoggerFactory.getLogger((VpaasServerConstants.LOGGER_MQTT_IDLE_TIMEOUT_LOG));

    public static void doSessionLog(Channel channel,
                                    MqttMessageType mqttMessageType, long cost, boolean status,
                                    Object msg) {

        String type = "";
        if (mqttMessageType != null) {
            type = mqttMessageType.name();
        }

        LoggerUtil.infoIfNeed(LOGGER_MQTT_SESSION_OP_LOG, "{0} {1} {2}", cost, status, msg);

        MonitorHelper.fastCompassOneKey("SESSION_OP_LOG", type, 1, cost, status);

    }

    public static void doIdleTimeoutLog(Channel channel, int expireSeconds, String clientId) {

        // 如果是单独的判断是否要打日志，就不要用threadcontext来做上下文判断，直接判断就可以。
        boolean isLog = LogNeedUtils.isDoPartOfLog(channel);
        if (isLog) {
            LoggerUtil.info(LOGGER_MQTT_IDLE_TIMEOUT_LOG, "{0} {1}", expireSeconds, clientId);
        }

        MonitorHelper.fastCompassOneKey("IDLE_TIMEOUT_LOG", String.valueOf(expireSeconds), 1, 0, true);
    }
}
