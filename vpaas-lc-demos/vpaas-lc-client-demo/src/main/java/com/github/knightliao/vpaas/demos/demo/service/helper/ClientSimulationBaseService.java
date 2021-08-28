package com.github.knightliao.vpaas.demos.demo.service.helper;

import com.github.knightliao.vpaas.demos.demo.support.enums.ClientRunEnums;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/29 01:43
 */
public class ClientSimulationBaseService {

    protected static String USER_NAME_FORMAT = "siginType|accessKey|instanceId|clientVer|-1";
    protected static int CONNECT_TIMEOUT_MS = 5000;
    protected ClientRunEnums clientRunEnums;
}
