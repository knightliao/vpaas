package com.github.knightliao.vpaas.lc.client.service.impl;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.github.knightliao.vpaas.lc.client.IMyLcClient;
import com.github.knightliao.vpaas.lc.client.netty.client.ILcClient;
import com.github.knightliao.vpaas.lc.client.netty.client.impl.LcClientImpl;
import com.github.knightliao.vpaas.lc.client.support.dto.ClientOptions;
import com.github.knightliao.vpaas.lc.client.support.dto.LcClientParam;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.RequestMsg;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.ResponseMsg;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * @author knightliao
 * @date 2021/8/8 23:28
 */
public class MyLcClientImpl implements IMyLcClient {

    private ILcClient lcClient = null;

    @Override
    public Channel getChannel() {
        return lcClient.getChannel();
    }

    @Override
    public void addEventListener(EventListener eventListener) {
        if (lcClient != null) {
            lcClient.addEventListener(eventListener);
        }
    }

    @Override
    public IMyLcClient newClient(ClientOptions clientOptions) {

        LcClientImpl client = new LcClientImpl();
        client.setServerList(getServerList(clientOptions.getServerList()));
        client.setServiceName(clientOptions.getClientName());

        client.getLcServiceParam().setSocketType(clientOptions.getSocketType());
        client.getLcClientParam().setConnectTimeout(clientOptions.getConnectTimeout());
        client.getLcServiceParam().setCheckHeartbeat(true);

        //
        lcClient = client;

        return this;
    }

    @Override
    public SocketAddress getCurTargetServer() {

        if (lcClient != null) {
            return lcClient.getCurTargetServer();
        }

        return null;
    }

    @Override
    public ChannelFuture connect() {
        if (lcClient != null) {
            return lcClient.connect();
        }

        return null;
    }

    @Override
    public void reconnect() {
        if (lcClient != null) {
            lcClient.reconnect();
        }
    }

    @Override
    public void close() {
        if (lcClient != null) {
            lcClient.close();
        }
    }

    @Override
    public LcClientParam getParam() {
        if (lcClient != null) {
            return lcClient.getParam();
        }
        return null;
    }

    @Override
    public ChannelFuture connectWait() {
        if (lcClient != null) {
            ChannelFuture future = lcClient.connect();
            return future.awaitUninterruptibly();
        }
        return null;
    }

    @Override
    public boolean connectWait(int timeoutMs) {
        if (lcClient != null) {
            ChannelFuture future = lcClient.connect();
            return future.awaitUninterruptibly(timeoutMs, TimeUnit.MILLISECONDS);
        }
        return false;
    }

    @Override
    public ChannelFuture send(Object message) {

        try {
            if (lcClient != null) {
                return lcClient.send(message);
            }

            return null;
        } catch (Exception ex) {

            lcClient.setErrorFlag();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ResponseMsg sendWithSync(RequestMsg requestMsg) {

        try {
            if (lcClient != null) {
                return lcClient.sendWithSync(requestMsg);
            }

            return null;
        } catch (Exception ex) {

            lcClient.setErrorFlag();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ResponseMsg sendWithSync(RequestMsg requestMsg, int timeout) {

        try {
            if (lcClient != null) {
                return lcClient.sendWithSync(requestMsg, timeout);
            }

            return null;
        } catch (Exception ex) {

            lcClient.setErrorFlag();
            throw new RuntimeException(ex);
        }
    }

    private List<SocketAddress> getServerList(String servers) {

        List<SocketAddress> list = new ArrayList<>();

        String[] serverDataArray = StringUtils.split(servers, ",");

        for (String serverData : serverDataArray) {

            String[] serverPair = StringUtils.split(serverData, ":");
            InetSocketAddress socketAddress = new InetSocketAddress(serverPair[0], Integer.parseInt(serverPair[1]));
            list.add(socketAddress);
        }

        return list;
    }
}
