package com.github.knightliao.vpaas.lc.server.connect.dispatch.executors;

import java.util.concurrent.ExecutorService;

import com.github.knightliao.vpaas.lc.server.connect.support.enums.ExecutorEnum;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/4 23:28
 */
public interface ILcServiceExecutorMgr {

    void newMessageExecutor(ExecutorEnum executorEnum, int corePoolSize, int maxPoolSize, int queueCapacity);

    ExecutorService getExecutor(ExecutorEnum executorEnum);

    int getExecutorActiveCount(ExecutorEnum executorEnum);

    long getExecutorCompletedTaskCount(ExecutorEnum executorEnum);

    long getExecutorTaskCount(ExecutorEnum executorEnum);

    int getExecutorLargestPoolSize(ExecutorEnum executorEnum);

    int getExecutorPoolSize(ExecutorEnum executorEnum);

    int getExecutorQueueSize(ExecutorEnum executorEnum);

    void shutdown();
}
