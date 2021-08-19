package com.github.knightliao.vpaas.common.rely.message.impl;

import com.github.knightliao.middle.idgen.IIdgenService;
import com.github.knightliao.middle.idgen.impl.IdgenServiceRedisImpl;
import com.github.knightliao.middle.redis.IMyRedisService;
import com.github.knightliao.vpaas.common.rely.message.IMessageIdService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/13 15:51
 */
@Slf4j
public class MessageIdServiceImpl implements IMessageIdService {

    private final static String KEY = "%s:messageid:num";

    private IIdgenService idgenService;

    public MessageIdServiceImpl(IMyRedisService myRedisService) {

        if (myRedisService == null) {
            throw new RuntimeException("myRedisService is null");
        }

        this.idgenService = new IdgenServiceRedisImpl(myRedisService);
    }

    @Override
    public int getNextMessageId(String bizKey) {

        try {

            while (true) {

                int nextMsgId = (int) (idgenService.getSequenceId(getKey(bizKey)) % Integer.MAX_VALUE);
                if (nextMsgId > 0) {
                    return nextMsgId;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }

    }

    @Override
    public long getNextMessageIdLong(String bizKey) {

        try {

            while (true) {

                long nextMsgId = (long) (idgenService.getSequenceId(getKey(bizKey)) % Long.MAX_VALUE);
                if (nextMsgId > 0) {
                    return nextMsgId;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }

    }

    private String getKey(String key) {
        return String.format(KEY, key);
    }
}
