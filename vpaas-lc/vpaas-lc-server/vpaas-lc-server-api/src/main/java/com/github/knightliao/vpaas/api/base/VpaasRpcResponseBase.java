package com.github.knightliao.vpaas.api.base;

import lombok.Data;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/22 15:11
 */
@Data
public class VpaasRpcResponseBase {

    private String traceId;
    private String info;
    private int statusCode;
}
