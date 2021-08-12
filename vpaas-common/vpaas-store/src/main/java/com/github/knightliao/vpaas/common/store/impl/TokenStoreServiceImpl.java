package com.github.knightliao.vpaas.common.store.impl;

import com.github.knightliao.middle.redis.IMyRedisService;
import com.github.knightliao.vpaas.common.store.ITokenStoreService;
import com.github.knightliao.vpaas.common.store.support.VpaasRedisKeyUtils;

/**
 * @author knightliao
 * @date 2021/8/12 17:25
 */
public class TokenStoreServiceImpl implements ITokenStoreService {

    private IMyRedisService myRedisService;

    public TokenStoreServiceImpl(IMyRedisService myRedisService) {
        if (myRedisService == null) {
            throw new RuntimeException("myRedisService is null");
        }

        this.myRedisService = myRedisService;
    }

    @Override
    public void putToken(int brokerId, String clientId, String token, int expireSeconds) {

        String tokenKey = VpaasRedisKeyUtils.getTokenKey(brokerId, clientId);
        myRedisService.set(tokenKey, expireSeconds, token);
    }

    @Override
    public String getToken(int brokerId, String clientId) {

        String tokenKey = VpaasRedisKeyUtils.getTokenKey(brokerId, clientId);
        return myRedisService.get(tokenKey);
    }

    @Override
    public boolean deleteToken(int brokerId, String clientId) {

        String tokenKey = VpaasRedisKeyUtils.getTokenKey(brokerId, clientId);
        return myRedisService.del(tokenKey);
    }
}
