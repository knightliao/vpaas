package com.github.knightliao.vpaas.common.rely.config.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.knightliao.middle.redis.IMyRedisBatchService;
import com.github.knightliao.middle.redis.IMyRedisService;
import com.github.knightliao.vpaas.common.store.ISessionStoreBatchService;
import com.github.knightliao.vpaas.common.store.ISessionStoreService;
import com.github.knightliao.vpaas.common.store.ITokenStoreService;
import com.github.knightliao.vpaas.common.store.impl.SessionStoreBatchServiceImpl;
import com.github.knightliao.vpaas.common.store.impl.SessionStoreServiceImpl;
import com.github.knightliao.vpaas.common.store.impl.TokenStoreServiceImpl;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/13 20:47
 */
@Configuration
public class StoreBeanConfiguration {

    @Bean("sessionStoreService")
    public ISessionStoreService sessionStoreService(IMyRedisService myRedisService) {
        return new SessionStoreServiceImpl(myRedisService);
    }

    @Bean("tokenStoreService")
    public ITokenStoreService tokenStoreService(IMyRedisService myRedisService) {
        return new TokenStoreServiceImpl(myRedisService);
    }

    @Bean("sessionStoreBatchService")
    public ISessionStoreBatchService sessionStoreBatchService(IMyRedisBatchService myRedisBatchService) {
        return new SessionStoreBatchServiceImpl(myRedisBatchService);
    }
}
