package com.github.knightliao.vpaas.common.utils.async.future;

/**
 * @author knightliao
 * @date 2021/8/4 15:34
 */
public interface IInvokeFutureListener {

    void operationComplete(IInvokeFuture future) throws Exception;
}
