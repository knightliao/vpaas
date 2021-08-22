package com.github.knightliao.vpaas.api.base;

import com.github.knightliao.middle.api.core.dto.MyBaseRequest;

import lombok.Data;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/22 15:33
 */
@Data
public class VpaasRpcRequestWithClientBase extends MyBaseRequest {

    // 区分业务
    private String group;

    // 请求来源标记
    private String from;

    // 强制打日志
    private boolean forcePrintLog;

    // 请求类型
    private Integer dsType;

    // 如果是client类型，
    private String clientId;

    // 如果是uid类型
    private Long uid;

    // 用于跟踪，如果不提供，则用 rpc中的traceid
    private String traceId;
}


