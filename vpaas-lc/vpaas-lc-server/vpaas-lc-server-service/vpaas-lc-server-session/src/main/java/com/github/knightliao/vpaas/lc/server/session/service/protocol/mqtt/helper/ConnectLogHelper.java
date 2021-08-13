package com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.knightliao.vpaas.lc.server.common.common.constants.VpaasServerConstants;
import com.github.knightliao.vpaas.lc.server.session.service.dto.VpaasCommonUserNameDto;
import com.github.knightliao.vpaas.lc.server.session.service.support.enums.ClientUserLoginoutEnum;
import com.github.knightliao.vpaas.lc.server.session.service.support.enums.VpaasConnectCommonEnum;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/13 14:24
 */
@Service
public class ConnectLogHelper {

    private static Logger logger = LoggerFactory.getLogger(VpaasServerConstants.LOGGER_CONNECT_RET_LOG);

    public void doConnectRealLog(VpaasConnectCommonEnum connectCommonEnum, String clientId,
                                 VpaasCommonUserNameDto userNameDto) {

        ClientUserLoginoutEnum clientUserLoginoutEnum = ClientUserLoginoutEnum.NULL;
        if (userNameDto != null) {
            clientUserLoginoutEnum = userNameDto.getClientUserLoginout();
        }

    }
}
