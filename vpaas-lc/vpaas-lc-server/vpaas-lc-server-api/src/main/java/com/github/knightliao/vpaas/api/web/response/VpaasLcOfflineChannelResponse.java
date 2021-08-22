package com.github.knightliao.vpaas.api.web.response;

import com.github.knightliao.vpaas.api.base.VpaasRpcResponseBase;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Tolerate;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/22 15:18
 */
@Data
@Builder
@ToString(callSuper = true)
public class VpaasLcOfflineChannelResponse extends VpaasRpcResponseBase {

    private int isRemove;

    @Tolerate
    public VpaasLcOfflineChannelResponse() {

    }
}