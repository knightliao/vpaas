package com.github.knightliao.vpaas.common.store.support;

/**
 * @author knightliao
 * @date 2021/8/12 11:08
 */
public class VpaasRedisKeyUtils {

    //
    public static String getTokenKey(int brokerId, String clientId) {
        return String.format("token_%d_%s", brokerId, clientId);
    }

    //
    public static String getSessionKey(int brokerId, String clientId) {

        return String.format("session_%d_%s", brokerId, clientId);
    }

    // uid - device list 的 hash key
    public static String getSessionUidDeviceHashKey(int brokerId, long uid) {

        return String.format("session_uid_key_%d_%s", brokerId, uid);
    }

    // uid会话过期时间
    public static int uidSessionExpireSeconds = 3600;

}
