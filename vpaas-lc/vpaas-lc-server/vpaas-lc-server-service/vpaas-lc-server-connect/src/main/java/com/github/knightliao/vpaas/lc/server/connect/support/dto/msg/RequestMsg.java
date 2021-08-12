package com.github.knightliao.vpaas.lc.server.connect.support.dto.msg;

import lombok.Data;
import lombok.ToString;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/7 18:01
 */
@Data
@ToString(callSuper = true)
public class RequestMsg extends BaseMessage {

    private Object message;
}
