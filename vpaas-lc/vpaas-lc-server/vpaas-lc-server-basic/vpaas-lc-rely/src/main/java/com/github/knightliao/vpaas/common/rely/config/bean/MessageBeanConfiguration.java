package com.github.knightliao.vpaas.common.rely.config.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.knightliao.middle.redis.IMyRedisService;
import com.github.knightliao.vpaas.common.rely.message.IMessageIdService;
import com.github.knightliao.vpaas.common.rely.message.impl.MessageIdServiceImpl;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/13 22:48
 */
@Configuration
public class MessageBeanConfiguration {

    @Bean("messageIdService")
    public IMessageIdService messageIdService(IMyRedisService myRedisService) {
        return new MessageIdServiceImpl(myRedisService);
    }
}