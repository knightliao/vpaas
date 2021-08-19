package com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.knightliao.middle.log.LoggerUtil;
import com.github.knightliao.vpaas.common.basic.constants.VpaasConstants;
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
                                 VpaasCommonUserNameDto userNameDto, long preUid) {

        ClientUserLoginoutEnum clientUserLoginoutEnum = ClientUserLoginoutEnum.NULL;
        long newUid = VpaasConstants.DEFAULT_ERROR_UID;
        if (userNameDto != null) {
            clientUserLoginoutEnum = userNameDto.getClientUserLoginout();
            newUid = userNameDto.getUid();
        }

        LoggerUtil.infoIfNeed(logger, "CONNECT {0} {1} {2} {3} {4} {5}",
                connectCommonEnum.getDesc(),
                clientId, preUid, newUid,
                clientUserLoginoutEnum.getDesc(), 0);
    }
}
