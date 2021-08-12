package com.github.knightliao.vpaas.lc.client.netty.client.impl;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import com.github.knightliao.middle.lang.exceptions.SocketRuntimeException;
import com.github.knightliao.middle.log.LoggerUtil;
import com.github.knightliao.vpaas.lc.client.netty.client.ILcClient;
import com.github.knightliao.vpaas.lc.client.support.dto.LcClientParam;

import io.netty.channel.ChannelFuture;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/8 17:58
 */
@Data
@Slf4j
public class LcClientImpl extends BaseClient implements ILcClient {

    protected String serviceName;

    private List<SocketAddress> serverList;

    // 是否发生异常
    protected AtomicBoolean errorFlag = new AtomicBoolean(false);

    // 是否正在断线重连
    protected AtomicBoolean reConnecting = new AtomicBoolean(false);

    // 重连总次数
    protected Map<SocketAddress, AtomicLong> reconnectTimesMap = new HashMap<>();

    @Override
    public LcClientParam getParam() {
        return super.getLcClientParam();
    }

    @Override
    public ChannelFuture connect() {
        return connect(true);
    }

    @Override
    public SocketAddress getCurTargetServer() {
        return curServer;
    }

    @Override
    public void reconnect() {

        // 发送时 会自动重连
        this.errorFlag.set(true);

        reConnectInner();
    }

    @Override
    public void setErrorFlag() {
        this.errorFlag.set(true);
    }

    @Override
    public ChannelFuture connect(boolean sync) {

        // random server
        List<SocketAddress> copyList = new ArrayList<>(serverList);
        Collections.shuffle(copyList);

        // 一个一个的进行连接
        for (SocketAddress server : copyList) {

            try {
                // 进行连接
                return super.connect(server, sync);
            } catch (Exception ex) {
                log.error(ex.toString());
            }
        }

        throw new SocketRuntimeException("cannot connect to server[" + serverList + "]");
    }

    //
    // 断线重连，一次只能一个线程在重连，其它均不重连
    //
    private void reConnectInner() {

        if (!errorFlag.get()) {
            return;
        }

        // close connect first
        close();

        try {

            // 防止并发重连 CAS
            if (reConnecting.compareAndSet(false, true)) {

                // 先重连当前服务器
                boolean isConnect = reconnectToCurServer();

                // 不行的话，再重连到其它机器
                if (!isConnect) {
                    reconnectToOtherServer();
                }
            }
        } finally {

            reConnecting.set(false);
        }
    }

    private boolean reconnectToOtherServer() {

        // random server
        List<SocketAddress> copyList = new ArrayList<>(serverList);
        Collections.shuffle(copyList);

        // 重连不上当前server, 则尝试重连到serverlist里的其它机器
        for (SocketAddress server : copyList) {

            if (server.equals(curServer)) {
                continue;
            }

            try {

                ChannelFuture future = this.connect(server, true);
                future.await();
                if (future.isSuccess() && future.cause() == null) {

                    LoggerUtil.info(log, "RECONNECT {0} 尝试其它机器重连到 {1} {2} 成功", serviceName,
                            server, incrReconnectTimeAndGet(server));
                    errorFlag.set(false);
                    return true;
                } else {

                    throw new Exception(future.cause());
                }

            } catch (Exception ex) {

                LoggerUtil.error(log, "RECONNECT {0} 尝试其它机器重连到 {1} {2} 失败!", serviceName, server,
                        incrReconnectTimeAndGet(server));
            }
        }

        return false;
    }

    // 重连当前服务器
    private boolean reconnectToCurServer() {

        for (int i = 0; i <= 1; ++i) {

            try {

                ChannelFuture future = this.connect(curServer, true);
                future.await();

                if (future.isSuccess() && future.cause() == null) {
                    LoggerUtil.info(log, "RECONNECT {0} 尝试当前机器第 {1}/{2} 次重连到 {3} 成功", serviceName, i,
                            incrReconnectTimeAndGet(curServer), curServer);
                    errorFlag.set(false);
                    return true;
                } else {

                    throw new Exception(future.cause());
                }

            } catch (Exception ex) {

                LoggerUtil.error(log, "RECONNECT {0} 尝试当前机器第 {1}/{2} 次重连到 {3} 失败! {4}", serviceName, i,
                        incrReconnectTimeAndGet(curServer), curServer, ex.toString());
            }
        }

        return false;
    }

    private Long incrReconnectTimeAndGet(SocketAddress socketAddress) {

        if (reconnectTimesMap.containsKey(socketAddress)) {
            return reconnectTimesMap.get(socketAddress).incrementAndGet();
        }

        AtomicLong atomicLong = new AtomicLong(2L);
        reconnectTimesMap.put(socketAddress, atomicLong);
        return atomicLong.get();
    }
}
