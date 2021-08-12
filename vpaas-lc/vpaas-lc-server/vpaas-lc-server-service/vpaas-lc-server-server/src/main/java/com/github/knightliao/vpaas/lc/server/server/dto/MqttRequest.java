package com.github.knightliao.vpaas.lc.server.server.dto;

import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Data;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/5 21:14
 */
@Data
public class MqttRequest {

    private byte[] payload;
    private MqttQoS qps = MqttQoS.AT_LEAST_ONCE;
    private boolean retained = false;
    private boolean dup = false;
    private int messageId;

    public MqttRequest() {
        this.setPayload(new byte[0]);
    }

    public MqttRequest(byte[] payload) {
        this.setPayload(payload);
    }

    public void clearPayload() {
        this.payload = new byte[0];
    }

    public void setPayload(byte[] payload) {

        if (payload == null) {
            throw new NullPointerException();
        } else {
            this.payload = payload;
        }
    }
}
