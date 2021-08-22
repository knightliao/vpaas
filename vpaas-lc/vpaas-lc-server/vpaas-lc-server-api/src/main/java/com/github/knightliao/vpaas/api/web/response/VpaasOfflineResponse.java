package com.github.knightliao.vpaas.api.web.response;

import com.github.knightliao.vpaas.api.base.VpaasRpcResponseBase;
import com.github.knightliao.vpaas.api.support.enums.VpaasLcOfflineEnum;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Tolerate;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/22 15:10
 */
@Data
@Builder
@ToString(callSuper = true)
public class VpaasOfflineResponse extends VpaasRpcResponseBase {

    private VpaasLcOfflineEnum vpaasLcOfflineEnum;

    @Tolerate
    public VpaasOfflineResponse() {

    }
}
