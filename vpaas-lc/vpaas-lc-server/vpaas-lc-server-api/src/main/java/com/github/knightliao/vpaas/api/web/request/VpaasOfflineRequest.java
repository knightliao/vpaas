package com.github.knightliao.vpaas.api.web.request;

import com.github.knightliao.vpaas.api.base.VpaasRpcRequestBase;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Tolerate;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/22 15:16
 */
@Data
@Builder
@ToString(callSuper = true)
public class VpaasOfflineRequest extends VpaasRpcRequestBase {

}
