package com.github.knightliao.vpaas.processor.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.github.knightliao.middle.msg.domain.consumer.IMessageListener;
import com.github.knightliao.middle.msg.domain.consumer.subscribe.ISourceSubscribe;
import com.github.knightliao.middle.msg.impl.kafka.consumer.subscribe.KafkaSequenceSubscribe;
import com.google.common.base.Preconditions;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/10/28 22:30
 */
@Service
public class VpaasMesasageProcessorKafkaBean {

    @Resource
    private VpaasKafkaMessaggeConsumerConfig vpaasKafkaMessaggeConsumerConfig;

    @Resource
    private IMessageListener messageListener;

    private ISourceSubscribe sourceSubscribe;

    @PostConstruct
    public void start() throws Exception {

        Preconditions.checkState(StringUtils.isNotBlank(vpaasKafkaMessaggeConsumerConfig.getKafkaConsumeTopics()));
        Preconditions.checkState(StringUtils.isNotBlank(vpaasKafkaMessaggeConsumerConfig.getKafkaConsumeBroker()));

        sourceSubscribe = new KafkaSequenceSubscribe(3, "consumeKafkaMsgEvent",
                vpaasKafkaMessaggeConsumerConfig.getKafkaConsumeTopics(),
                vpaasKafkaMessaggeConsumerConfig.getKafkaConsumeBroker(),
                vpaasKafkaMessaggeConsumerConfig.getKafkaMsgConsumeGroup(),
                messageListener,
                vpaasKafkaMessaggeConsumerConfig.getPollTimeoutMs(),
                vpaasKafkaMessaggeConsumerConfig.getPrintFrequency());
        sourceSubscribe.start();
    }

    @PreDestroy
    public void shutdown() {

        if (sourceSubscribe != null) {
            sourceSubscribe.shutdown();
        }
    }
}
