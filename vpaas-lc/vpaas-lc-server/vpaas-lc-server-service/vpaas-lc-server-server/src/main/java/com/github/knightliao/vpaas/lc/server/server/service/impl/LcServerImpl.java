package com.github.knightliao.vpaas.lc.server.server.service.impl;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.github.knightliao.vpaas.common.utils.lang.Tuple2;
import com.github.knightliao.vpaas.common.utils.log.LoggerUtil;
import com.github.knightliao.vpaas.common.utils.net.NettyUtils;
import com.github.knightliao.vpaas.lc.server.connect.dispatch.dispatcher.LcEventDispatcherFactory;
import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.netty.handler.LcCountHandler;
import com.github.knightliao.vpaas.lc.server.connect.netty.server.LcServerContext;
import com.github.knightliao.vpaas.lc.server.connect.netty.service.LcService;
import com.github.knightliao.vpaas.lc.server.server.dto.MqttRequest;
import com.github.knightliao.vpaas.lc.server.server.dto.ServerParam;
import com.github.knightliao.vpaas.lc.server.server.service.IMyLcServer;
import com.github.knightliao.vpaas.lc.server.server.service.impl.helper.LcServerHelper;
import com.github.knightliao.vpaas.lc.server.server.service.impl.helper.ServerPipeline;
import com.github.knightliao.vpaas.lc.server.server.service.impl.status.StatusLcServerImpl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * 核心server
 *
 * @author knightliao
 * @date 2021/8/5 21:18
 */
@Slf4j
public class LcServerImpl extends LcService implements IMyLcServer {

    // 所有连接通道 map, 这个对象未来可能非常大，到时可能要进行分割
    // 这个理论上不会有死的channel, 但实际上无法保证，只有channelGroup 能保证死的channel自动消失
    // 初始化大小 100W
    protected ConcurrentHashMap<String, LcWrappedChannel> channels = new ConcurrentHashMap<>(2 * 512 * 1024);
    protected ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    // 记录最大的channel 数量，以前是用 map的size, 压力有点大
    protected AtomicLong channelMaxSize = new AtomicLong(0L);

    // 服务参数
    private ServerParam serverParam = new ServerParam();

    // boss group
    protected EventLoopGroup bossGroup;

    // work group
    protected EventLoopGroup workerGroup;

    // 核心启动器
    protected ServerBootstrap bootstrap;

    // 统计的handler
    protected LcCountHandler lcCountHandler;

    public LcServerImpl() {

        super();
        //
        this.getLcServiceParam().setOpenExecutor(true);
    }

    @Override
    protected void init() {

        super.init();

        // new core threads
        Tuple2<EventLoopGroup, EventLoopGroup> tuple2 = LcServerHelper.newCoreThreads(lcServiceParam.getBossCount(),
                lcServiceParam.getWorkerCount());
        bossGroup = tuple2.getFirst();
        workerGroup = tuple2.getSecond();

        // event dispatcher
        newEventDispatcher();

        // event listener
        addEventListener();

        // counter handler
        if (lcServiceParam.isOpenCount()) {
            lcCountHandler = new LcCountHandler();
        }

        // context
        if (!(this instanceof StatusLcServerImpl)) {
            LcServerContext.getContext().setServer(this);
            LcServerContext.getContext().setBrokerId(serverParam.getBrokerId());
        }
    }

    private void newEventDispatcher() {
        lcEventDispatcher = LcEventDispatcherFactory.getEventDispatcherDefaultImpl(this);
    }

    private void addEventListener() {

    }

    @Override
    public void shutdown() {

        try {

            //
            if (workerGroup != null) {
                log.info("start to shutdown workerGroup");
                workerGroup.shutdownGracefully().sync();
            }

            //
            if (bossGroup != null) {
                log.info("start to shutdown bossGroup");
                bossGroup.shutdownGracefully().sync();
            }

        } catch (InterruptedException ex) {
            log.error(ex.toString(), ex);
        }
    }

    @Override
    public ServerParam getServerParam() {
        return serverParam;
    }

    @Override
    public ChannelFuture send(LcWrappedChannel channel, String topic, MqttRequest mqttRequest)
            throws InterruptedException {
        return null;
    }

    @Override
    public ChannelFuture bind() {

        // init instance
        init();

        // server bootstrap
        newServerBootstrap();

        // bind
        ChannelFuture future = bindSocket();

        // status server
        addStatusServer();

        return future;
    }

    private ChannelFuture bindSocket() {

        // bind
        final InetSocketAddress socketAddress = new InetSocketAddress(getLcServiceParam().getPort());
        ChannelFuture future = bootstrap.bind(socketAddress);

        // exit handler
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {

                channelFuture.await();
                if (channelFuture.isSuccess()) {
                    LoggerUtil.info(log, "server started, listening on {0} {1}", socketAddress, lcServiceParam.getIp());
                } else {
                    LoggerUtil.error(log, "Failed to start server {0} {1}, caused by {2}", socketAddress,
                            lcServiceParam.getIp(), channelFuture.cause());
                }
            }
        });

        return future;
    }

    private void addStatusServer() {

        if (serverParam.isOpenStatus()) {
            LcServerHelper.listenToStatusServer(serverParam.getStatusPort());
        }
    }

    private void newServerBootstrap() {

        // bootstrap
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, getLcServiceParam().isKeepAlive());
        bootstrap.childOption(ChannelOption.TCP_NODELAY, getLcServiceParam().isTcpNoDelay());
        bootstrap.channel(NettyUtils.useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);

        // 分发器
        bootstrap.childHandler(ServerPipeline.getChildChannelHandler(this, lcEventDispatcher, lcCountHandler));
    }

    @Override
    public LcWrappedChannel getChannel(String channelId) {
        if (channelId != null) {
            return channels.get(channelId);
        }
        return null;
    }

    @Override
    public LcWrappedChannel getChannelByGroup(ChannelId channelId) {
        return (LcWrappedChannel) channelGroup.find(channelId);
    }

    @Override
    public Map<String, LcWrappedChannel> getChannels() {
        return channels;
    }

    @Override
    public long incrChannelMaxSize(boolean incr) {
        if (incr) {
            return channelMaxSize.incrementAndGet();
        }
        return channelMaxSize.decrementAndGet();
    }

    @Override
    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }
}
