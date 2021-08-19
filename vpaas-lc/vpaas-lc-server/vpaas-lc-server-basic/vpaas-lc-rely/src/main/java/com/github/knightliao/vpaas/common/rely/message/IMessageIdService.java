package com.github.knightliao.vpaas.common.rely.message;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/13 15:49
 */
public interface IMessageIdService {

    int getNextMessageId(String bizKey);

    long getNextMessageIdLong(String bizKey);
}
