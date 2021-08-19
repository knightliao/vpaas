package com.github.knightliao.vpaas.lc.server.start.test.mqtt.client;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/17 14:25
 */
@Slf4j
public class MqttEclipseClientTest {

    private final static String clientId = "GID_XXX@@CLientID_123";
    private final static String userName = "siginType|accessKey|instanceId|clientVer|-1";

    public static void main(String[] args) {

        final String broker = "tcp://127.0.0.1:6000";

        MemoryPersistence persistence = new MemoryPersistence();
        final String topic = "server/";

        try {

            final MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            log.info("connecting to broker {}", broker);

            final MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setServerURIs(new String[] {broker});
            connectOptions.setPassword("123456".toCharArray());
            connectOptions.setUserName(userName);
            connectOptions.setCleanSession(true);
            connectOptions.setKeepAliveInterval(90);
            connectOptions.setAutomaticReconnect(true);
            connectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);

            sampleClient.setCallback(null);

            sampleClient.connect(connectOptions);

            for (int i = 0; i < 10000; ++i) {

                try {

                    String content = "hello from client " + i;

                    //
                    final MqttMessage message = new MqttMessage(content.getBytes());
                    message.setQos(0);

                    log.info("public message: {}", content);

                    //sampleClient.publish(topic, message);

                    Thread.sleep(5000);

                } catch (Exception ex) {

                    ex.printStackTrace();
                    System.exit(0);
                }
            }

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }
}
