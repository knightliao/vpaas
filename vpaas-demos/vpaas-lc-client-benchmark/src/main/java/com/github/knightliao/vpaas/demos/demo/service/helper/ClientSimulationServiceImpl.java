package com.github.knightliao.vpaas.demos.demo.service.helper;

import java.net.SocketAddress;

import com.github.knightliao.middle.lang.constants.PackConstants;
import com.github.knightliao.vpaas.demos.demo.service.ClientSimulationService;
import com.github.knightliao.vpaas.demos.demo.service.listener.MessageEventListenerDemo;
import com.github.knightliao.vpaas.demos.demo.support.dto.ClientDemoContext;
import com.github.knightliao.vpaas.demos.demo.support.log.DemoLogUtils;
import com.github.knightliao.vpaas.lc.client.IMyLcClient;
import com.github.knightliao.vpaas.lc.client.service.helper.VpaasClientFactory;
import com.github.knightliao.vpaas.lc.client.support.dto.ClientOptions;
import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.channel.ChannelKeyUtils;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.SocketType;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectPayload;
import io.netty.handler.codec.mqtt.MqttConnectVariableHeader;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/29 01:43
 */
@Slf4j
public class ClientSimulationServiceImpl extends ClientSimulationBaseService implements ClientSimulationService {

    private IMyLcClient myLcClient;

    public ClientSimulationServiceImpl(
            ClientDemoContext clientDemoContext) {
        super(clientDemoContext);
    }

    @Override
    public LcWrappedChannel getChannel() {
        return (LcWrappedChannel) myLcClient.getChannel();
    }

    @Override
    public boolean connect(String serverListStr) {

        ClientOptions clientOptions = new ClientOptions();
        clientOptions.setSocketType(SocketType.MQTT);
        clientOptions.setServerList(serverListStr);
        clientOptions.setClientName(clientDemoContext.getClientId());

        //
        myLcClient = VpaasClientFactory.getMyLcClientImpl().newClient(clientOptions);
        myLcClient.addEventListener(new MessageEventListenerDemo(this, clientDemoContext));

        return connect(serverListStr);
    }

    @Override
    public SocketAddress getCurTargetServer() {
        return myLcClient.getCurTargetServer();
    }

    @Override
    public void login() {

        if (clientDemoContext.getUid() == null || clientDemoContext.getUid() == PackConstants.DEFAULT_ERROR_UID) {

            clientDemoContext.setUid(DEFAULT_UID);
            DemoLogUtils.info(clientRunEnum, "user_login {0} {1}", clientDemoContext.getClientId(),
                    clientDemoContext.getUid());

            sendConnectCmd();
        }
    }

    @Override
    public void logout() {

        if (clientDemoContext.getUid() == null || clientDemoContext.getUid() == PackConstants.DEFAULT_ERROR_UID) {
            return;
        }

        DemoLogUtils.info(clientRunEnum, "user_logout {0} {1}", clientDemoContext.getClientRunEnum(),
                clientDemoContext.getUid());
        clientDemoContext.setUid(PackConstants.DEFAULT_ERROR_UID);

        sendConnectCmd();
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
    public void setConnectStatus(String scene, boolean connectOk, boolean isAuth) {

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

    private void sendConnectCmd() {

        String curUserName = fetchConnectUserName();

        int seconds = clientDemoContext.getKeepAliveTimeoutSecond();

        MqttConnectPayload mqttConnectPayload = new MqttConnectPayload(clientDemoContext.getClientId(), "", "",
                curUserName, "");
        MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(SocketType.MQTT.getDesc(),
                (byte) 4, true, true, false, 0, false, false, seconds);

        MqttConnectMessage mqttConnectMessage = (MqttConnectMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.CONNECT, true, MqttQoS.AT_MOST_ONCE, true, 0),
                mqttConnectVariableHeader, mqttConnectPayload);

        ChannelFuture channelFuture = myLcClient.send(mqttConnectMessage);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {

                future.await();

                if (future.isSuccess()) {

                    //
                    Channel channel = future.channel();
                    setupChannelAttribute(channel, clientDemoContext.getClientId(), clientDemoContext.getUid());

                    DemoLogUtils.info(clientRunEnum, "connectSend {0} {1}", clientDemoContext.getClientId(),
                            clientDemoContext.getUid());

                } else {

                    //
                    DemoLogUtils.info(clientRunEnum, "Noconnected {0} {1}", clientDemoContext.getClientId(),
                            clientDemoContext.getUid());
                }
            }
        });

        channelFuture.awaitUninterruptibly();
    }

    private void setupChannelAttribute(Channel channel, String clientId, Long uid) {

        ChannelKeyUtils.setChannelClientSessionAttribute(channel, clientId);
        if (uid != null) {
            ChannelKeyUtils.setChannelClientSessionUidAttribute(channel, uid);
        } else {
            ChannelKeyUtils.setChannelClientSessionUidAttribute(channel, PackConstants.DEFAULT_ERROR_UID);
        }
    }
}
