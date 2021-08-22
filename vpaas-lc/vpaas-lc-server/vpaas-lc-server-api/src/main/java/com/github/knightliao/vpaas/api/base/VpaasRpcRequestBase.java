package com.github.knightliao.vpaas.api.base;

import com.github.knightliao.middle.api.core.dto.MyBaseRequest;

import lombok.Data;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/22 15:12
 */
@Data
public class VpaasRpcRequestBase extends VpaasRpcRequestWithClientBase {

    // 请求来源标记
    private String from;

    // 强制打日志，默认为false
    private boolean forcePrintLog;
}
