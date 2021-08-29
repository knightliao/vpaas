package com.github.knightliao.vpaas.demos.demo.service.helper;

import com.github.knightliao.middle.lang.constants.PackConstants;
import com.github.knightliao.vpaas.demos.demo.support.dto.ClientDemoContext;
import com.github.knightliao.vpaas.demos.demo.support.enums.ClientRunEnum;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/29 01:43
 */
public class ClientSimulationBaseService {

    protected static String USER_NAME_FORMAT = "siginType|accessKey|instanceId|clientVer|%d";
    protected static int CONNECT_TIMEOUT_MS = 5000;
    protected ClientRunEnum clientRunEnum;

    protected Long DEFAULT_UID;
    protected ClientDemoContext clientDemoContext;
    protected String currentToken = "";

    public ClientSimulationBaseService(ClientDemoContext clientDemoContext) {
        this.clientDemoContext = clientDemoContext;
        this.DEFAULT_UID = clientDemoContext.getUid();
        this.clientRunEnum = clientDemoContext.getClientRunEnum();
    }

    protected String fetchConnectUserName() {

        if (clientDemoContext.getUid() != null && clientDemoContext.getUid() != PackConstants.DEFAULT_ERROR_UID) {
            return String.format(USER_NAME_FORMAT, clientDemoContext.getUid());
        }

        return String.format(USER_NAME_FORMAT, -1);
    }
}
