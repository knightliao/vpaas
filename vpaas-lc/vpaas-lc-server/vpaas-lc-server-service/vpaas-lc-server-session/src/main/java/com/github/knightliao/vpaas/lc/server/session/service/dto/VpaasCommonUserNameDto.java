package com.github.knightliao.vpaas.lc.server.session.service.dto;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.knightliao.vpaas.common.basic.constants.VpaasConstants;
import com.github.knightliao.vpaas.lc.server.session.service.protocol.mqtt.helper.ConnectHelper;
import com.github.knightliao.vpaas.lc.server.session.service.support.enums.ClientUserLoginoutEnum;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.experimental.Builder;
import lombok.experimental.Tolerate;

/**
 * 由 鉴权模式名称(0)、AccessKey(1)、InstanceId(2)、VERSION(3)、UID(4) 三部分组成，以竖线（|）分隔。
 * 可参考 https://help.aliyun.com/document_detail/54225.html
 *
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/12 16:51
 */
@Data
@Builder
public class VpaasCommonUserNameDto {

    private static final String PATTERN = "\\|";

    private String siginType;
    private String accessKey;
    private String instanceId;
    private String clientVer;
    private long uid;

    private ClientUserLoginoutEnum clientUserLoginout;

    @Tolerate
    public VpaasCommonUserNameDto() {

    }

    public static VpaasCommonUserNameDto parse(Channel channel, String userName) {

        String[] stringTmp = userName.split(PATTERN, -1);
        List<String> strings = Arrays.asList(stringTmp);

        if (strings.size() < 3) {
            return null;
        }

        String siginType = strings.get(0);
        String accessKey = strings.get(1);
        String instanceId = strings.get(2);
        String clientVer = strings.get(3);

        if (StringUtils.isEmpty(siginType) || StringUtils.isEmpty(accessKey)
                || StringUtils.isEmpty(instanceId)) {
            return null;
        }

        Long uid;
        try {
            uid = Long.parseLong(strings.get(4));
        } catch (Exception ex) {
            uid = VpaasConstants.DEFAULT_ERROR_UID;
        }

        //
        ClientUserLoginoutEnum clientUserLoginout = ConnectHelper.getClientUserLoginoutEnum(channel, uid);

        return VpaasCommonUserNameDto.builder().
                siginType(siginType).
                accessKey(accessKey).
                instanceId(instanceId).
                clientVer(clientVer).
                uid(uid).
                clientUserLoginout(clientUserLoginout).
                build();
    }
}
