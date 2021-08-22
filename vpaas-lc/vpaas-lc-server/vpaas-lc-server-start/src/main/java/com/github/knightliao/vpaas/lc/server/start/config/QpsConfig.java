package com.github.knightliao.vpaas.lc.server.start.config;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.knightliao.middle.api.support.aop.QpsInterceptor;
import com.github.knightliao.middle.lang.callback.IMyMethodCallback;
import com.github.knightliao.middle.thread.MyThreadContext;
import com.github.knightliao.vpaas.common.rely.config.VpaasConfig;
import com.github.knightliao.vpaas.lc.server.start.support.utils.RequestAopUtils;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/22 18:47
 */
@Configuration
public class QpsConfig {

    @Resource
    private VpaasConfig vpaasConfig;

    @Bean
    public QpsInterceptor qpsAspect() {
        return new QpsInterceptor(new MyQpsCallback());
    }

    protected class MyQpsCallback implements IMyMethodCallback<ProceedingJoinPoint> {

        @Override
        public void preDo(ProceedingJoinPoint joinPoint) {

            try {
                //
                MyThreadContext.init();

                //
                RequestAopUtils.doLogConfig(joinPoint,
                        vpaasConfig.isWebRequestLogDebug(), vpaasConfig.getLogUidSet(), vpaasConfig.getLogClientSet());

            } finally {

                MyThreadContext.clean();
            }
        }

        @Override
        public void afterDo(ProceedingJoinPoint joinPoint) {

        }
    }
}
