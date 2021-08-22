package com.github.knightliao.vpaas.lc.server.start.support.utils;

import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;

import com.github.knightliao.middle.thread.MyThreadContext;
import com.github.knightliao.vpaas.api.base.VpaasRpcRequestWithClientBase;

import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/22 18:50
 */
@Slf4j
public class RequestAopUtils {

    public static void doLogConfig(ProceedingJoinPoint joinPoint, boolean openAll,
                                   Set<Long> uids, Set<String> clients) {

        try {

            if (openAll) {
                // 打印所有日志
                MyThreadContext.putPrintLogKey(true);

            } else {

                // 细粒度控制
                Object[] signatureArgs = joinPoint.getArgs();
                for (Object arg : signatureArgs) {

                    if (arg != null) {
                        if (arg instanceof VpaasRpcRequestWithClientBase) {

                            //  满足业务要求
                            VpaasRpcRequestWithClientBase request = (VpaasRpcRequestWithClientBase) arg;
                            String clientId = request.getClientId();
                            Long uid = request.getUid();
                            if (uids.contains(uid) || clients.contains(clientId)) {
                                MyThreadContext.putPrintLogKey(true);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }
    }
}
