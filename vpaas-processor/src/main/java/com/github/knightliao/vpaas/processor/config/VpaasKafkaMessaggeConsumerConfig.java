package com.github.knightliao.vpaas.processor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/10/28 22:19
 */
@Service
@Slf4j
@Getter
public class VpaasKafkaMessaggeConsumerConfig {

    @Value("$vpaas.processor.kafkaConsumeBroker}")
    private String kafkaConsumeBroker;

    // consume 线程数
    @Value("$vpaas.processor.kafkaConsumeNumber:6}")
    private String kafkaConsumeNumber;

    // consume 的topic列表
    @Value("$vpaas.processor.kafkaConsumeTopics:}")
    private String kafkaConsumeTopics;

    // consume
    @Value("$vpaas.processor.kafkaMsgConsumeTopicConnect:}")
    private String kafkaMsgConsumeTopicConnect;

    // consume
    @Value("$vpaas.processor.kafkaMsgConsumeTopicPublic:}")
    private String kafkaMsgConsumeTopicPublic;

    // consume
    @Value("$vpaas.processor.kafkaMsgConsumeGroup:}")
    private String kafkaMsgConsumeGroup;

    //
    @Value("$vpaas.processor.pollTimeoutMs:3000}")
    private int pollTimeoutMs;

    //
    @Value("$vpaas.processor.printFrequency:5000}")
    private int printFrequency;
}
