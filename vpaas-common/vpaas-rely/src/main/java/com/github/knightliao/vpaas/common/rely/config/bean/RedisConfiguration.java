package com.github.knightliao.vpaas.common.rely.config.bean;

import static java.util.Arrays.stream;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.github.knightliao.middle.log.LoggerUtil;
import com.github.knightliao.middle.redis.IMyRedisBatchService;
import com.github.knightliao.middle.redis.IMyRedisService;
import com.github.knightliao.middle.redis.RedisServerFactory;
import com.github.knightliao.middle.redis.impl.MyRedisBatchServiceImpl;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/13 21:14
 */
@EnableConfigurationProperties
@PropertySource("classpath:application.properties")
@Slf4j
@Configuration
@EnableCaching
public class RedisConfiguration {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.password}")
    private String redisPassword;

    @Bean("jedisCluster")
    public JedisCluster buildBasicJedisCluster() {

        LoggerUtil.info(log, "redisCluster: {0}", redisHost);

        Set<HostAndPort> nodes = stream(redisHost.split(","))
                .map(host -> host.split(":"))
                .filter(strings -> strings.length == 2)
                .map(strings -> new HostAndPort(strings[0], Integer.valueOf(strings[1])))
                .collect(Collectors.toSet());

        LoggerUtil.info(log, "redisCluster ok");

        return isNotEmpty(redisPassword)
                ? new JedisCluster(nodes, 2000, 200, 5, redisPassword, new MyJedisPoolConfig())
                : new JedisCluster(nodes, 2000, 200, 5, new MyJedisPoolConfig());
    }

    public static class MyJedisPoolConfig extends GenericObjectPoolConfig {

        public MyJedisPoolConfig() {

            setTestWhileIdle(true);
            setMinEvictableIdleTimeMillis(60000);
            setTimeBetweenEvictionRunsMillis(30000);
            setNumTestsPerEvictionRun(-1);
            setTestOnBorrow(false);
            setTestOnReturn(true);
            setMaxTotal(GenericObjectPoolConfig.DEFAULT_MAX_TOTAL * 5 * 2);
            setMaxIdle(GenericObjectPoolConfig.DEFAULT_MAX_IDLE * 3);
            setMinIdle(GenericObjectPoolConfig.DEFAULT_MIN_IDLE * 2);
        }
    }

    @Bean("myRedisService")
    public IMyRedisService myRedisService(JedisCluster jedisCluster) {

        return RedisServerFactory.getMyRedisService(jedisCluster);
    }

    @Bean("myRedisBatchService")
    public IMyRedisBatchService myRedisBatchService(JedisCluster jedisCluster) {
        return new MyRedisBatchServiceImpl(jedisCluster, redisHost, redisPassword);
    }
}


