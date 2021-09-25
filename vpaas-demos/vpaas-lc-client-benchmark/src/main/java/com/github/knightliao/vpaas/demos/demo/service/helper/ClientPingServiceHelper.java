package com.github.knightliao.vpaas.demos.demo.service.helper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.time.StopWatch;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.knightliao.middle.log.LoggerUtil;
import com.github.knightliao.middle.trace.MyTraceUtils;
import com.github.knightliao.vpaas.demos.demo.service.ClientSimulationService;
import com.github.knightliao.vpaas.demos.demo.support.constants.VpaasClientDemoConstants;
import com.github.knightliao.vpaas.demos.demo.support.dto.ClientDemoContext;
import com.github.knightliao.vpaas.lc.server.connect.support.utils.LcServiceTraceHelper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/29 12:50
 */
@Slf4j
public class ClientPingServiceHelper {

    private static final Logger THREAD_INFO = LoggerFactory.getLogger(VpaasClientDemoConstants.THREAD_INFO);

    private ClientDemoContext clientDemoContext;
    private ClientSimulationService clientSimulationService;
    private ClientReconnectServiceHelper clientReconnectServiceHelper;

    // 下次Ping的时间
    private long nextPingTimeMills = System.currentTimeMillis();
    private AtomicBoolean canNextTest = new AtomicBoolean(true);

    // 执行线程池
    private static ExecutorService messageExecutor = new ThreadPoolExecutor(
            1000,
            5000,
            6L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10000),
            new BasicThreadFactory.Builder().namingPattern("ClientPingService " + "-%d").daemon(true).build(),
            new ThreadPoolExecutor.AbortPolicy());

    public ClientPingServiceHelper(ClientDemoContext clientDemoContext,
                                   ClientSimulationService clientSimulationService,
                                   ClientReconnectServiceHelper clientReconnectServiceHelper) {

        this.clientDemoContext = clientDemoContext;
        this.clientSimulationService = clientSimulationService;
        this.clientReconnectServiceHelper = clientReconnectServiceHelper;
    }

    // ping操作，不重试，失败就返回
    public void runPing() {

        try {

            //
            if (clientSimulationService.getChannel() != null) {
                LcServiceTraceHelper.startTraceAndSession(clientSimulationService.getChannel());
            }

            //
            doPing();

        } catch (Throwable ex) {

            log.error(ex.toString(), ex);
        }
    }

    private void doPing() {

        clientSimulationService.ping();

        if (clientDemoContext.getLoginPerhaps() > 0
                && RandomUtils.nextInt() % clientDemoContext.getLoginPerhaps() == 0) {
            clientSimulationService.login();

        } else if (clientDemoContext.getLogoutPerhaps() > 0
                && RandomUtils.nextInt() % clientDemoContext.getLogoutPerhaps() == 0) {
            clientSimulationService.logout();

        } else if (clientDemoContext.getDisconnectPerhaps() > 0
                && RandomUtils.nextInt() % clientDemoContext.getDisconnectPerhaps() == 0) {
            clientSimulationService.sendDisconnectCmd();
        }
    }

    public static void printThreadTaskData() {

        if (messageExecutor instanceof ThreadPoolExecutor) {

            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) messageExecutor;

            LoggerUtil.info(THREAD_INFO, "activeCount:{0} largestPoolSize:{1} poolSize:{2} queueSize:{3} "
                            + "taskCount:{4}", threadPoolExecutor.getActiveCount(),
                    threadPoolExecutor.getLargestPoolSize(),
                    threadPoolExecutor.getPoolSize(), threadPoolExecutor.getQueue(), threadPoolExecutor.getTaskCount());
        }
    }

    public void run() {

        try {
            // 未到时间
            if (System.currentTimeMillis() < nextPingTimeMills || !canNextTest.get()) {
                return;
            }

            messageExecutor.execute(new Runnable() {
                @Override
                public void run() {

                    //
                    MyTraceUtils.newReqId();

                    // 必须满足 时间到了 并且上次已经执行完成
                    if (System.currentTimeMillis() > nextPingTimeMills && canNextTest.compareAndSet(true, false)) {

                        try {
                            StopWatch stopWatch = new StopWatch();
                            stopWatch.start();

                            // 连接的情况下，执行Ping
                            if (clientSimulationService.isMqttProtocolRealConnected()) {

                                //
                                LcServiceTraceHelper.startTraceAndSession(clientSimulationService.getChannel());

                                //
                                runPing();

                                // 正常的定时
                                stopWatch.stop();
                                nextPingTimeMills =
                                        System.currentTimeMillis()
                                                + clientDemoContext.getKeepAliveTimeoutSecond() * 1000L
                                                - stopWatch.getTime();
                            } else {

                                // 进行重连
                                clientReconnectServiceHelper.justReconnect();

                                // 重连快速一次
                                stopWatch.stop();
                                nextPingTimeMills =
                                        System.currentTimeMillis()
                                                + clientDemoContext.getKeepAliveTimeoutSecond() * 1000L
                                                - stopWatch.getTime();
                                log.info("long " + clientDemoContext.getClientId() + " " +
                                        new DateTime(nextPingTimeMills));
                            }

                        } catch (Throwable ex) {

                            log.error(ex.toString(), ex);
                            nextPingTimeMills =
                                    System.currentTimeMillis()
                                            + (long) clientDemoContext.getKeepAliveTimeoutSecond() * 2 * 1000;

                        } finally {

                            LcServiceTraceHelper.clearChannelSession();

                            //
                            canNextTest.set(true);
                        }
                    }
                }
            });
        } catch (Throwable ex) {

            log.error(ex.toString());
        }
    }
}
