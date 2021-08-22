package com.github.knightliao.vpaas.common.rely.config.bean;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.knightliao.middle.idem.IIdemService;
import com.github.knightliao.middle.idem.impl.IdemServiceImpl;
import com.github.knightliao.middle.idgen.aop.IdGenAop;
import com.github.knightliao.middle.lock.IMyDistributeLock;
import com.github.knightliao.middle.lock.impl.MyRedisDistributeLockImpl;
import com.github.knightliao.middle.redis.IMyRedisService;
import com.github.knightliao.middle.redis.aop.RedisAop;
import com.github.knightliao.middle.redis.schedule.RedisMetricSchedule;
import com.github.knightliao.vpaas.common.rely.config.VpaasConfig;

import redis.clients.jedis.JedisCluster;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/19 18:04
 */
@Configuration
public class RedisAdvConfiguration {

    @Resource
    private VpaasConfig vpaasConfig;

    @Bean("idemService")
    public IIdemService idemService(IMyRedisService myRedisService) {

        return new IdemServiceImpl(myRedisService);
    }

    @Bean("myDistributeLock")
    public IMyDistributeLock distributeLock(JedisCluster jedisCluster) {

        return new MyRedisDistributeLockImpl(jedisCluster);
    }

    @Bean("redisAop")
    public RedisAop redisAop() {

        RedisAop redisAop = new RedisAop();
        redisAop.setMetricStatistic(true);
        if (vpaasConfig.isLogMiddlewareDebug()) {
            redisAop.setDebug(true);
        }

        return redisAop;
    }

    @Bean("idGenAop")
    public IdGenAop idGenAop() {

        IdGenAop idGenAop = new IdGenAop();
        idGenAop.setMetricStatistic(true);
        if (vpaasConfig.isLogMiddlewareDebug()) {
            idGenAop.setDebug(true);
        }

        return idGenAop;
    }

    @Bean("redisMetricSchedule")
    public RedisMetricSchedule redisMetricSchedule(JedisCluster jedisCluster) {

        return new RedisMetricSchedule("vpaas_redis", jedisCluster);
    }
}
