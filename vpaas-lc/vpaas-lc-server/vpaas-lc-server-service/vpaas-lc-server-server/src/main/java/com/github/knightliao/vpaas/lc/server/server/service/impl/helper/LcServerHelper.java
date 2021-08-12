package com.github.knightliao.vpaas.lc.server.server.service.impl.helper;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.github.knightliao.middle.log.LoggerUtil;
import com.github.knightliao.vpaas.common.utils.net.NettyUtils;
import com.github.knightliao.vpaas.lc.server.connect.netty.server.ILcServer;
import com.github.knightliao.vpaas.lc.server.connect.netty.service.ILcService;
import com.github.knightliao.vpaas.lc.server.server.service.impl.status.StatusLcServerImpl;

import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/5 21:35
 */
@Slf4j
public class LcServerHelper {

    public static ImmutablePair<EventLoopGroup, EventLoopGroup> newCoreThreads(int bossNum, int worNum) {

        if (NettyUtils.useEpoll()) {
            return newEpollThreads(bossNum, worNum);
        } else {

            return newNormalThreads(bossNum, worNum);
        }
    }

    private static ImmutablePair<EventLoopGroup, EventLoopGroup> newEpollThreads(int bossNum, int workNum) {

        EventLoopGroup bossGroup = new EpollEventLoopGroup(bossNum, new ThreadFactory() {

            private AtomicInteger index = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {

                return new Thread(r, "LINUX_BOSS_" + index.incrementAndGet());
            }
        });

        EventLoopGroup workGroup = new EpollEventLoopGroup(workNum, new ThreadFactory() {

            private AtomicInteger index = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {

                return new Thread(r, "LINUX_WORK_" + index.incrementAndGet());
            }
        });

        return new ImmutablePair<>(bossGroup, workGroup);
    }

    private static ImmutablePair<EventLoopGroup, EventLoopGroup> newNormalThreads(int bossNum, int workNum) {

        EventLoopGroup bossGroup = new NioEventLoopGroup(bossNum, new ThreadFactory() {

            private AtomicInteger index = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {

                return new Thread(r, "BOSS" + index.incrementAndGet());
            }
        });

        EventLoopGroup workGroup = new NioEventLoopGroup(workNum, new ThreadFactory() {

            private AtomicInteger index = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {

                return new Thread(r, "WORK_" + index.incrementAndGet());
            }
        });

        return new ImmutablePair<>(bossGroup, workGroup);
    }

    public static void listenToStatusServer(int statusPort) {

        ILcServer lcServer = new StatusLcServerImpl();
        ILcService lcService = (ILcService) lcServer;

        lcService.getLcServiceParam().setPort(statusPort);
        lcService.getLcServiceParam().setTcpNoDelay(true);
        lcService.getLcServiceParam().setKeepAlive(true);

        ChannelFuture channelFuture = lcServer.bind();
        channelFuture.awaitUninterruptibly();

        LoggerUtil.info(log, "statusServer start");
    }

}
