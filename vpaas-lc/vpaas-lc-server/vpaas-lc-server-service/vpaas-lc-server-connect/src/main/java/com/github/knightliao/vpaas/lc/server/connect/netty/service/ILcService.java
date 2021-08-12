package com.github.knightliao.vpaas.lc.server.connect.netty.service;

import java.util.EventListener;
import java.util.LinkedHashMap;
import java.util.List;

import com.github.knightliao.vpaas.lc.server.connect.dispatch.executors.ILcServiceExecutorMgr;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.param.LcServiceParam;

import io.netty.channel.ChannelHandler;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/4 23:10
 */
public interface ILcService {

    void addEventListener(EventListener listener);

    void addChannelHandler(String key, ChannelHandler handler);

    LinkedHashMap<String, ChannelHandler> getHandlers();

    void setHandlers(LinkedHashMap<String, ChannelHandler> handlers);

    List<EventListener> getEventListeners();

    void setEventListeners(List<EventListener> listeners);

    ILcServiceExecutorMgr getLcServiceExecutorMgr();

    LcServiceParam getLcServiceParam();

    void shutdown();

}


