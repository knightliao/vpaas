package com.github.knightliao.vpaas.common.rely.config.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.knightliao.middle.idem.IIdemService;
import com.github.knightliao.middle.idem.impl.IdemServiceImpl;
import com.github.knightliao.middle.lock.IMyDistributeLock;
import com.github.knightliao.middle.lock.impl.MyRedisDistributeLockImpl;
import com.github.knightliao.middle.redis.IMyRedisService;

import redis.clients.jedis.JedisCluster;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/19 18:04
 */
@Configuration
public class RedisAdvConfiguration {

    @Bean("idemService")
    public IIdemService idemService(IMyRedisService myRedisService) {

        return new IdemServiceImpl(myRedisService);
    }

    @Bean("myDistributeLock")
    public IMyDistributeLock distributeLock(JedisCluster jedisCluster) {

        return new MyRedisDistributeLockImpl(jedisCluster);
    }
}
