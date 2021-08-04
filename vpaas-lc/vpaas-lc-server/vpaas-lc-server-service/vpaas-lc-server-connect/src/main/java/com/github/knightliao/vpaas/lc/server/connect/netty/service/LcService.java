package com.github.knightliao.vpaas.lc.server.connect.netty.service;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.LinkedHashMap;
import java.util.List;

import com.github.knightliao.vpaas.lc.server.connect.dispatch.dispatcher.ILcEventDispatcher;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.param.LcServiceParam;

import io.netty.channel.ChannelHandler;

/**
 * @author knightliao
 * @date 2021/8/4 23:10
 */
public abstract class LcService {

    protected LcServiceParam lcServiceParam = new LcServiceParam();

    // channel handlers
    protected LinkedHashMap<String, ChannelHandler> handlers = new LinkedHashMap<>();

    // 事件监听列表
    protected List<EventListener> eventListenerList = new ArrayList<>();

    // 事件分发器
    protected ILcEventDispatcher lcEventDispatcher;

    // 线程管理器

}


