package com.github.knightliao.vpaas.demos.demo.service.impl;

import java.net.SocketAddress;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.github.knightliao.vpaas.demos.demo.service.ClientSimulationService;
import com.github.knightliao.vpaas.demos.demo.service.helper.ClientSimulationBaseService;
import com.github.knightliao.vpaas.demos.demo.support.dto.ClientDemoContext;
import com.github.knightliao.vpaas.demos.demo.support.log.DemoLogUtils;
import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;

import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/29 10:43
 */
@Slf4j
public class ClientSimulationEclipseImpl extends ClientSimulationBaseService implements ClientSimulationService {

    private MqttClient mqttClient = null;

    private ScheduledExecutorService scheduledExecutorService;

    public ClientSimulationEclipseImpl(ClientDemoContext clientDemoContext,
                                       ScheduledExecutorService scheduledExecutorService) {
        super(clientDemoContext);
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @Override
    public LcWrappedChannel getChannel() {
        return null;
    }

    @Override
    public boolean connect(String serverListStr) {

        final String broker = String.format("tcp://%s", serverListStr);

        try {
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(broker, clientDemoContext.getClientId(), persistence, scheduledExecutorService);

            final MqttConnectOptions connectOptions = new MqttConnectOptions();
            DemoLogUtils.info(clientRunEnum, "connecting {0}", broker);

            // 自动重连
            connectOptions.setAutomaticReconnect(true);
            connectOptions.setServerURIs(new String[] {broker});
            connectOptions.setUserName(fetchConnectUserName());
            connectOptions.setKeepAliveInterval(10);
            // 暂时没有密码
            connectOptions.setPassword("".toCharArray());
            connectOptions.setCleanSession(false);
            connectOptions.setKeepAliveInterval(clientDemoContext.getKeepAliveTimeoutSecond());
            connectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);

            //
            mqttClient.setCallback(callback());

            // set connect
            IMqttToken mqttToken = mqttClient.connectWithResult(connectOptions);
            mqttToken.waitForCompletion();

            //
            DemoLogUtils.info(clientRunEnum, "connected {0}", mqttToken.getClient().getClientId());
            if (mqttToken.getException() != null) {
                log.error(mqttToken.getException().toString());
                return false;
            }

            return true;

        } catch (Exception ex) {

            DemoLogUtils.error(ex, clientRunEnum, "{0}", ex.toString());
            return false;
        }
    }

    private MqttCallbackExtended callback() {

        return new MqttCallbackExtended() {

            @Override
            public void connectionLost(Throwable cause) {
                DemoLogUtils.info(clientRunEnum, "connectionLost {0} {1}", clientDemoContext.getClientId(),
                        cause.toString());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                DemoLogUtils.info(clientRunEnum, "messageArrived {0} {1}", topic, message.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                DemoLogUtils.info(clientRunEnum, "deliveryComplete {0} ", token.getMessageId());
            }

            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                DemoLogUtils.info(clientRunEnum, "connectComplete {0} {1} {2} {3}", clientRunEnum.getDesc(),
                        clientDemoContext.getClientId(), reconnect, serverURI);
            }
        };
    }

    @Override
    public SocketAddress getCurTargetServer() {
        return null;
    }

    @Override
    public void login() {

    }

    @Override
    public void logout() {

    }

    @Override
    public boolean reconnect() {
        return false;
    }

    @Override
    public void sendDisconnectCmd() {

    }

    @Override
    public void doPubAck(int messageId) {

    }

    @Override
    public void ping() {

    }

    @Override
    public void setToken(String token) {

    }

    @Override
    public boolean isMqttProtocolRealConnected() {
        return false;
    }

    @Override
    public boolean isTcpConnected() {
        return false;
    }
}
