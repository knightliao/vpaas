package com.github.knightliao.vpaas.lc.server.connect.dispatch.dispatcher.impl;

import java.util.EventListener;
import java.util.concurrent.Future;

import com.github.knightliao.middle.log.LoggerUtil;
import com.github.knightliao.vpaas.lc.server.connect.dispatch.dispatcher.ILcEventDispatcher;
import com.github.knightliao.vpaas.lc.server.connect.netty.channel.LcWrappedChannel;
import com.github.knightliao.vpaas.lc.server.connect.netty.listener.LcChannelEventListener;
import com.github.knightliao.vpaas.lc.server.connect.netty.listener.LcExceptionEventListener;
import com.github.knightliao.vpaas.lc.server.connect.netty.listener.LcMessageEventListener;
import com.github.knightliao.vpaas.lc.server.connect.netty.service.LcService;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.ExecutorEnum;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.LcEventBehaviorEnum;
import com.github.knightliao.vpaas.lc.server.connect.support.utils.LcServiceTraceHandler;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 事件分发器
 *
 * @author knightliao
 * @date 2021/8/4 23:15
 */
@Slf4j
public class LcEventDispatcherImpl implements ILcEventDispatcher {

    private LcService lcService;

    public LcEventDispatcherImpl(LcService lcService) {
        if (lcService == null) {
            throw new IllegalArgumentException("service is null");
        }

        this.lcService = lcService;
    }

    @Override
    public void dispatchMessageEvent(ChannelHandlerContext channelHandlerContext, LcWrappedChannel channel,
                                     Object msg) {

        if (channel == null) {
            return;
        }

        try {

            if (lcService.getLcServiceParam().isOpenExecutor()) {

                Future<Boolean> future =
                        lcService.getLcServiceExecutorMgr().getExecutor(ExecutorEnum.MESSAGE).submit(() -> {

                            doMessageEvent(channelHandlerContext, channel, msg);
                            return true;
                        });
            } else {

                doMessageEvent(channelHandlerContext, channel, msg);
            }
        } finally {

        }
    }

    @Override
    public void dispatchChannelEvent(ChannelHandlerContext channelHandlerContext,
                                     LcWrappedChannel channel) {

        if (channel == null) {
            return;
        }

        if (lcService.getLcServiceParam().isOpenExecutor()) {

            lcService.getLcServiceExecutorMgr().getExecutor(ExecutorEnum.CHANNEL).execute(new Runnable() {
                @Override
                public void run() {

                    doChannelEvent(channelHandlerContext, channel);
                }
            });
        }
    }

    @Override
    public void dispatchExceptionEvent(ChannelHandlerContext channelHandlerContext,
                                       LcWrappedChannel channel, Throwable cause) {

        if (channel == null) {
            return;
        }

        if (lcService.getLcServiceParam().isOpenExecutor()) {

            lcService.getLcServiceExecutorMgr().getExecutor(ExecutorEnum.EXCEPTION).execute(new Runnable() {
                @Override
                public void run() {

                    doExceptionEvent(channelHandlerContext, channel, cause);
                }
            });
        }
    }

    private void doChannelEvent(final ChannelHandlerContext channelHandlerContext, final LcWrappedChannel channel) {

        if (channel == null) {
            return;
        }

        try {

            //
            LcServiceTraceHandler.startTraceAndSession(channel);

            for (EventListener listener : lcService.getEventListeners()) {

                if (listener instanceof LcChannelEventListener) {

                    LcChannelEventListener channelEventListener = (LcChannelEventListener) listener;

                    LcEventBehaviorEnum eventBehaviorEnum;
                    final boolean isConnected = channel.isActive();
                    if (isConnected) {
                        eventBehaviorEnum = channelEventListener.channelActive(channelHandlerContext, channel);
                    } else {
                        eventBehaviorEnum = channelEventListener.channelInactive(channelHandlerContext, channel);
                    }

                    if (LcEventBehaviorEnum.BREAK.equals(eventBehaviorEnum)) {
                        break;
                    }
                }
            }
        } catch (Exception ex) {

            dispatchExceptionEvent(channelHandlerContext, channel, ex);
            log.error(ex.toString(), ex);

        } finally {

            LcServiceTraceHandler.stopTrace();
        }
    }

    private void doMessageEvent(final ChannelHandlerContext channelHandlerContext, final LcWrappedChannel channel,
                                final Object msg) {

        if (channel == null) {
            return;
        }

        try {

            //
            LcServiceTraceHandler.startTraceAndSession(channel);

            for (EventListener listener : lcService.getEventListeners()) {
                if (listener instanceof LcMessageEventListener) {

                    LcEventBehaviorEnum eventBehaviorEnum =
                            ((LcMessageEventListener) listener).channelRead(channelHandlerContext, channel, msg);

                    if (LcEventBehaviorEnum.BREAK.equals(eventBehaviorEnum)) {
                        break;
                    }
                }
            }

        } catch (Exception ex) {

            dispatchExceptionEvent(channelHandlerContext, channel, ex);

        } finally {

            LcServiceTraceHandler.stopTrace();
        }
    }

    private void doExceptionEvent(final ChannelHandlerContext channelHandlerContext, final LcWrappedChannel channel,
                                  final Throwable cause) {

        if (channel == null) {
            return;
        }

        try {

            //
            LcServiceTraceHandler.startTraceAndSession(channel);

            for (EventListener listener : lcService.getEventListeners()) {
                if (listener instanceof LcExceptionEventListener) {

                    LcEventBehaviorEnum eventBehaviorEnum =
                            ((LcExceptionEventListener) listener)
                                    .exceptionCaught(channelHandlerContext, channel, cause);

                    if (LcEventBehaviorEnum.BREAK.equals(eventBehaviorEnum)) {
                        break;
                    }
                }
            }

        } catch (Exception ex) {

            LoggerUtil.error(ex, log, "Failed to dispatch exception event on channel.");

        } finally {

            LcServiceTraceHandler.stopTrace();
        }
    }

}
