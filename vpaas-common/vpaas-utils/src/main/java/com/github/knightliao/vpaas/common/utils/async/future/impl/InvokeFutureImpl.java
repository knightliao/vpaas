package com.github.knightliao.vpaas.common.utils.async.future.impl;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.knightliao.vpaas.common.utils.async.future.IInvokeFuture;
import com.github.knightliao.vpaas.common.utils.async.future.IInvokeFutureListener;
import com.github.knightliao.vpaas.common.utils.exceptions.SocketRuntimeException;
import com.github.knightliao.vpaas.common.utils.security.ParamAssertUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @date 2021/8/4 15:41
 */
@Slf4j
public class InvokeFutureImpl implements IInvokeFuture {

    private Object result;
    private AtomicBoolean done = new AtomicBoolean(false);
    private AtomicBoolean success = new AtomicBoolean(false);
    private Semaphore semaphore = new Semaphore(0);
    private Throwable cause;

    private List<IInvokeFutureListener> listeners = new ArrayList<>();

    @Override
    public void addListener(IInvokeFutureListener listener) {

        ParamAssertUtil.assertArgumentNotNull(listener, "listener");

        notifyListener(listener);
        listeners.add(listener);
    }

    private void notifyListener(IInvokeFutureListener listener) {

        ParamAssertUtil.assertArgumentNotNull(listener, "listener");

        if (isDone()) {

            try {
                listener.operationComplete(this);

            } catch (Exception ex) {

                log.error("Failed to notify listener when operation completed", ex);
            }
        }
    }

    private void notifyListeners() {
        if (isDone()) {
            for (IInvokeFutureListener listener : listeners) {
                notifyListener(listener);
            }
        }
    }

    @Override
    public boolean isDone() {
        return done.get();
    }

    @Override
    public Object getResult() throws SocketRuntimeException {

        if (!isDone()) {
            try {
                semaphore.acquire();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }

        if (cause != null) {
            throw new SocketRuntimeException(cause);
        }
        return this.result;
    }

    @Override
    public void setResult(Object result) {

        this.result = result;
        done.set(true);
        success.set(true);

        semaphore.release(Integer.MAX_VALUE - semaphore.availablePermits());
        notifyListeners();
    }

    @Override
    public Object getResult(long timeout, TimeUnit unit) {

        if (!isDone()) {

            try {
                if (!semaphore.tryAcquire(timeout, unit)) {
                    setCause(new SocketTimeoutException("time out"));
                }
            } catch (InterruptedException ex) {
                throw new SocketRuntimeException(ex);
            }
        }

        if (cause != null) {
            throw new SocketRuntimeException(cause);
        }

        return this.result;
    }

    @Override
    public void setCause(Throwable cause) {

        this.cause = cause;
        done.set(true);
        success.set(false);
        semaphore.release(Integer.MAX_VALUE - semaphore.availablePermits());

        notifyListeners();
    }

    @Override
    public boolean isSuccess() {
        return success.get();
    }

    @Override
    public Throwable getCause() {
        return cause;
    }
}
