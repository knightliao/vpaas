package com.github.knightliao.vpaas.lc.server.connect.netty.service;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.LinkedHashMap;
import java.util.List;

import com.github.knightliao.vpaas.lc.server.connect.dispatch.dispatcher.ILcEventDispatcher;
import com.github.knightliao.vpaas.lc.server.connect.dispatch.executors.ILcServiceExecutorMgr;
import com.github.knightliao.vpaas.lc.server.connect.dispatch.executors.impl.LcServiceExecutorMgrImpl;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.param.LcServiceParam;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.ExecutorEnum;

import io.netty.channel.ChannelHandler;

/**
 * @author knightliao
 * @date 2021/8/4 23:10
 */
public abstract class LcService implements ILcService {

    protected LcServiceParam lcServiceParam = new LcServiceParam();

    // channel handlers
    protected LinkedHashMap<String, ChannelHandler> handlers = new LinkedHashMap<>();

    // 事件监听列表
    protected List<EventListener> eventListenerList = new ArrayList<>();

    // 事件分发器
    protected ILcEventDispatcher lcEventDispatcher;

    // 线程管理器
    protected ILcServiceExecutorMgr lcServiceExecutorMgr = new LcServiceExecutorMgrImpl();

    public LcService() {
        //
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(this, lcServiceExecutorMgr));
    }

    protected void init() {

        // 工作线程
        if (lcServiceParam.getWorkerCount() <= 0) {
            lcServiceParam.setWorkerCount(Runtime.getRuntime().availableProcessors() * 2);
        }

        if (lcServiceParam.getBossCount() <= 0) {
            lcServiceParam.setBossCount(1);
        }

        // 线程处理
        if (lcServiceParam.isOpenExecutor()) {

            lcServiceExecutorMgr.newMessageExecutor(ExecutorEnum.MESSAGE, lcServiceParam.getCorePoolSize(),
                    lcServiceParam.getMaximumPoolSize(), lcServiceParam.getQueueCapacity());

            lcServiceExecutorMgr.newMessageExecutor(ExecutorEnum.CHANNEL, lcServiceParam.getCorePoolChannelSize(),
                    lcServiceParam.getMaximumPoolChannelSize(), lcServiceParam.getQueueChannelCapacity());

            lcServiceExecutorMgr.newMessageExecutor(ExecutorEnum.EXCEPTION, lcServiceParam.getCorePoolExceptionSize(),
                    lcServiceParam.getMaximumPoolExceptionSize(), lcServiceParam.getQueueExceptionCapacity());
        }
    }

    @Override
    public void addEventListener(EventListener listener) {
        this.eventListenerList.add(listener);
    }

    @Override
    public void addChannelHandler(String key, ChannelHandler handler) {
        this.handlers.put(key, handler);
    }

    @Override
    public LinkedHashMap<String, ChannelHandler> getHandlers() {
        return handlers;
    }

    @Override
    public void setHandlers(LinkedHashMap<String, ChannelHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public List<EventListener> getEventListeners() {
        return eventListenerList;
    }

    @Override
    public void setEventListeners(List<EventListener> listeners) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        this.eventListenerList = listeners;
    }

    @Override
    public ILcServiceExecutorMgr getLcServiceExecutorMgr() {
        return lcServiceExecutorMgr;
    }

    @Override
    public LcServiceParam getLcServiceParam() {
        return lcServiceParam;
    }

    @Override
    public abstract void shutdown();

    static class ShutdownHook extends Thread {

        private LcService lcService;
        private ILcServiceExecutorMgr lcServiceExecutorMgr;

        public ShutdownHook(LcService lcService, ILcServiceExecutorMgr iLcServiceExecutorMgr) {
            this.lcService = lcService;
            this.lcServiceExecutorMgr = iLcServiceExecutorMgr;
        }

        @Override
        public void run() {

            lcService.shutdown();

            lcServiceExecutorMgr.shutdown();
        }
    }
}


