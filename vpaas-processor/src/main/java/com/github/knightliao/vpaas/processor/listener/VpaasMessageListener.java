package com.github.knightliao.vpaas.processor.listener;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.knightliao.middle.msg.domain.consumer.IMessageListener;
import com.github.knightliao.middle.msg.domain.consumer.handler.IMessageHandler;
import com.github.knightliao.middle.msg.domain.consumer.handler.IMessageProcessService;
import com.github.knightliao.vpaas.processor.config.VpaasKafkaMessaggeConsumerConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/10/28 23:34
 */
@Slf4j
@Service
public class VpaasMessageListener implements IMessageListener {

    @Resource
    private IMessageHandler messageHandler;

    @Resource
    private VpaasKafkaMessaggeConsumerConfig vpaasKafkaMessaggeConsumerConfig;

    @Resource
    private IMessageProcessService vpaasHookClientMsgListenerImpl;

    @Resource
    private IMessageProcessService vpaasHookPublishMsgListenerImpl;

    @PostConstruct
    public void init() {

        messageHandler.register(vpaasKafkaMessaggeConsumerConfig.getKafkaMsgConsumeTopicConnect(),
                vpaasHookClientMsgListenerImpl);
        messageHandler.register(vpaasKafkaMessaggeConsumerConfig.getKafkaMsgConsumeTopicPublic(),
                vpaasHookPublishMsgListenerImpl);
    }

    @Override
    public void onMessage(String s, List<String> list, List<Object> list1) {

        messageHandler.process(s, list, list1);
    }
}
