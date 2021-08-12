package com.github.knightliao.vpaas.lc.server.connect.dispatch.executors.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import com.github.knightliao.vpaas.lc.server.connect.dispatch.executors.ILcServiceExecutorMgr;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.ExecutorEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * 消息、channel、异常、处理线程组
 *
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/4 23:28
 */
@Slf4j
public class LcServiceExecutorMgrImpl implements ILcServiceExecutorMgr {

    private Map<ExecutorEnum, ExecutorService> executorServiceMap = new HashMap<>();

    @Override
    public void newMessageExecutor(ExecutorEnum executorEnum, int corePoolSize, int maxPoolSize, int queueCapacity) {

        ExecutorService executorService = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(queueCapacity),
                new BasicThreadFactory.Builder().namingPattern(executorEnum.getDesc() + "-%d").daemon((true)).build(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        executorServiceMap.put(executorEnum, executorService);
    }

    @Override
    public ExecutorService getExecutor(ExecutorEnum executorEnum) {
        return executorServiceMap.get(executorEnum);
    }

    @Override
    public int getExecutorActiveCount(ExecutorEnum executorEnum) {

        ExecutorService executorService = getExecutor(executorEnum);
        if (executorService != null) {
            if (executorService instanceof ThreadPoolExecutor) {
                return ((ThreadPoolExecutor) executorService).getActiveCount();
            }
        }

        return -1;
    }

    @Override
    public long getExecutorCompletedTaskCount(ExecutorEnum executorEnum) {

        ExecutorService executorService = getExecutor(executorEnum);
        if (executorService != null) {
            if (executorService instanceof ThreadPoolExecutor) {
                return ((ThreadPoolExecutor) executorService).getCompletedTaskCount();
            }
        }

        return -1;
    }

    @Override
    public long getExecutorTaskCount(ExecutorEnum executorEnum) {

        ExecutorService executorService = getExecutor(executorEnum);
        if (executorService != null) {
            if (executorService instanceof ThreadPoolExecutor) {
                return ((ThreadPoolExecutor) executorService).getTaskCount();
            }
        }

        return -1;
    }

    @Override
    public int getExecutorLargestPoolSize(ExecutorEnum executorEnum) {

        ExecutorService executorService = getExecutor(executorEnum);
        if (executorService != null) {
            if (executorService instanceof ThreadPoolExecutor) {
                return ((ThreadPoolExecutor) executorService).getLargestPoolSize();
            }
        }

        return -1;
    }

    @Override
    public int getExecutorPoolSize(ExecutorEnum executorEnum) {

        ExecutorService executorService = getExecutor(executorEnum);
        if (executorService != null) {
            if (executorService instanceof ThreadPoolExecutor) {
                return ((ThreadPoolExecutor) executorService).getPoolSize();
            }
        }

        return -1;
    }

    @Override
    public int getExecutorQueueSize(ExecutorEnum executorEnum) {

        ExecutorService executorService = getExecutor(executorEnum);
        if (executorService != null) {
            if (executorService instanceof ThreadPoolExecutor) {
                return ((ThreadPoolExecutor) executorService).getQueue().size();
            }
        }

        return -1;
    }

    @Override
    public void shutdown() {

        for (ExecutorEnum executorEnum : executorServiceMap.keySet()) {

            try {
                log.info("start to shutdown " + executorEnum.toString());
                executorServiceMap.get(executorEnum).awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {

            }
        }
    }
}
