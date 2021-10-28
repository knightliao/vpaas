package com.github.knightliao.vpaas.processor.biz.listener;

import org.springframework.stereotype.Service;

import com.github.knightliao.middle.msg.domain.consumer.handler.IMessageProcessService;
import com.github.knightliao.middle.msg.support.exceptions.MessageProcessorException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/10/28 23:31
 */
@Slf4j
@Service("vpaasHookClientMsgListenerImpl")
public class VpaasHookClientMsgListenerImpl implements IMessageProcessService {

    @Override
    public boolean process(String s, Object o, Object o1) throws MessageProcessorException {
        return false;
    }

    @Override
    public Class<?> getClassType() {
        return null;
    }
}
