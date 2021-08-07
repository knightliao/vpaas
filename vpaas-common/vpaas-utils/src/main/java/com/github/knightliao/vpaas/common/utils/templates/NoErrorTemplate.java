package com.github.knightliao.vpaas.common.utils.templates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author knightliao
 * @date 2021/8/6 21:08
 */
public class NoErrorTemplate {

    private static final Logger log = LoggerFactory.getLogger(NoErrorTemplate.class);

    public NoErrorTemplate() {

    }

    public static void handle(Runnable callbakc) {
        try {
            callbakc.run();
        } catch (Throwable throwable) {
            log.error("处理回调失败", throwable);
        }
    }
}
