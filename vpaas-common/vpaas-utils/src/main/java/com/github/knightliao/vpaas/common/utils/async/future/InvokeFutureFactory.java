package com.github.knightliao.vpaas.common.utils.async.future;

import com.github.knightliao.vpaas.common.utils.async.future.impl.InvokeFutureImpl;

/**
 * @author knightliao
 * @date 2021/8/4 15:40
 */
public class InvokeFutureFactory {

    public static IInvokeFuture getInvokeFutureDefaultImpl() {

        return new InvokeFutureImpl();
    }
}
