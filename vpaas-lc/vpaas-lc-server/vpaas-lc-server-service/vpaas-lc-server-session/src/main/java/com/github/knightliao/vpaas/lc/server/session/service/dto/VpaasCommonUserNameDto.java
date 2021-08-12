package com.github.knightliao.vpaas.lc.server.session.service.dto;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.experimental.Builder;
import lombok.experimental.Tolerate;

/**
 * 由鉴权模式名称、AccessKey、Instance ID三部分组成，以竖线（|）分隔。
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
    public static final String TOKEN = "Token";

    private String siginType;
    private String accessKey;
    private String instanceId;

    @Tolerate
    public VpaasCommonUserNameDto() {

    }

    public static VpaasCommonUserNameDto parse(String userName) {

        String[] stringTmp = userName.split(PATTERN, -1);
        List<String> strings = Arrays.asList(stringTmp);

        if (strings.size() != 3) {
            return null;
        }

        String siginType = strings.get(0);
        String accessKey = strings.get(1);
        String instanceId = strings.get(2);

        if (StringUtils.isEmpty(siginType) || StringUtils.isEmpty(accessKey) || StringUtils.isEmpty(instanceId)) {
            return null;
        }

        return VpaasCommonUserNameDto.builder().
                siginType(siginType).
                accessKey(accessKey).
                instanceId(instanceId)
                .build();
    }
}
