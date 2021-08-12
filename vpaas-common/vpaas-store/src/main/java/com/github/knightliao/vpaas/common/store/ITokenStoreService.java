package com.github.knightliao.vpaas.common.store;

/**
 * @author knightliao
 * @date 2021/8/12 17:22
 */
public interface ITokenStoreService {

    void putToken(int brokerId, String clientId, String token, int expireSeconds);

    String getToken(int brokerId, String clientId);

    boolean deleteToken(int brokerId, String clientId);
}
