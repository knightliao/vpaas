package com.github.knightliao.vpaas.processor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.github.knightliao.middle.msg.domain.consumer.handler.IMessageHandler;
import com.github.knightliao.middle.msg.impl.kafka.consumer.handler.MessageHandlerImpl;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/10/28 23:35
 */
@Service
public class VpaasMessageProcessorBean {

    @Bean("messageHandler")
    public IMessageHandler messageHandler() {

        return new MessageHandlerImpl();
    }
}
