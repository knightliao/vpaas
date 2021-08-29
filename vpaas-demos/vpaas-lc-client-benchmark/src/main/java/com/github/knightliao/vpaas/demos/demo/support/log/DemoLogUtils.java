package com.github.knightliao.vpaas.demos.demo.support.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.knightliao.middle.log.LoggerUtil;
import com.github.knightliao.vpaas.demos.demo.support.constants.VpaasClientDemoConstants;
import com.github.knightliao.vpaas.demos.demo.support.enums.ClientRunEnum;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/29 11:12
 */
public class DemoLogUtils {

    private static final Logger OTHERS_LOG = LoggerFactory.getLogger(VpaasClientDemoConstants.OTHERS);
    private static final Logger SINGLE_LOG = LoggerFactory.getLogger(VpaasClientDemoConstants.SINGLE_LOG);
    private static final Logger MESS_LOG = LoggerFactory.getLogger(VpaasClientDemoConstants.MESS_LOG);

    public static void info(ClientRunEnum clientRunEnum, String template, Object... params) {
        LoggerUtil.info(getLogger(clientRunEnum), template, params);
    }

    public static void error(ClientRunEnum clientRunEnum, String template, Object... params) {
        LoggerUtil.error(getLogger(clientRunEnum), template, params);
    }

    public static void error(Throwable throwable, ClientRunEnum clientRunEnum, String template, Object... params) {
        LoggerUtil.error(throwable, getLogger(clientRunEnum), template, params);
    }

    private static Logger getLogger(ClientRunEnum clientRunEnum) {
        if (clientRunEnum.equals(ClientRunEnum.MESS)) {
            return MESS_LOG;

        } else if (clientRunEnum.equals(ClientRunEnum.SINGLE)) {
            return SINGLE_LOG;
        }

        return OTHERS_LOG;
    }
}
