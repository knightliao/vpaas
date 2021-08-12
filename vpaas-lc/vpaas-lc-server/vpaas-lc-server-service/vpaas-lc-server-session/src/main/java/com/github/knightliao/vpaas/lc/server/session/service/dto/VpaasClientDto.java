package com.github.knightliao.vpaas.lc.server.session.service.dto;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.experimental.Builder;
import lombok.experimental.Tolerate;

/**
 * 格式为 <GroupID>@@@<DeviceID>
 * 可参考 https://help.aliyun.com/document_detail/42420.htm
 *
 * @author knightliao
 * @date 2021/8/12 16:27
 */
@Data
@Builder
public class VpaasClientDto {

    private String groupId;

    private String utdid;

    private String clientId;

    @Tolerate
    public VpaasClientDto() {

    }

    public static VpaasClientDto parse(String content) {

        if (StringUtils.isEmpty(content)) {
            return null;
        }

        if (content.length() > 64) {
            return null;
        }

        String[] ret = StringUtils.split(content, "@@@");
        if (ret.length != 2) {
            return null;
        }

        if (StringUtils.isEmpty(ret[0]) || StringUtils.isEmpty(ret[1])) {
            return null;
        }

        return VpaasClientDto.builder().clientId(content).groupId(ret[0]).utdid(ret[1]).build();
    }
}
