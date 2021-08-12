package com.github.knightliao.vpaas.common.store.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.knightliao.middle.redis.IMyRedisService;
import com.github.knightliao.vpaas.common.basic.constants.VpaasConstants;
import com.github.knightliao.vpaas.common.store.ISessionStoreService;
import com.github.knightliao.vpaas.common.store.dto.SessionStoreDto;
import com.github.knightliao.vpaas.common.store.support.VpaasRedisKeyUtils;

/**
 * @author knightliao
 * @date 2021/8/12 10:46
 */
public class SessionStoreServiceImpl implements ISessionStoreService {

    private IMyRedisService myRedisService;

    public SessionStoreServiceImpl(IMyRedisService myRedisService) {
        this.myRedisService = myRedisService;
    }

    @Override
    public void put(SessionStoreDto sessionStoreDto, int expire) {

        if (sessionStoreDto == null) {
            return;
        }

        SessionStoreDto.SessionStoreKeyDto sessionStoreKeyDto = sessionStoreDto.getSessionStoreKeyDto();
        if (sessionStoreKeyDto == null) {
            return;
        }

        String key = VpaasRedisKeyUtils.getSessionKey(sessionStoreKeyDto.getBrokerId(),
                sessionStoreKeyDto.getClientId());
        myRedisService.set(key, expire, sessionStoreDto);

        if (sessionStoreKeyDto.getUid() == VpaasConstants.DEFAULT_ERROR_UID) {
            putUser(sessionStoreKeyDto.getBrokerId(), sessionStoreKeyDto.getClientId(),
                    sessionStoreKeyDto.getUid(), sessionStoreDto);
        }
    }

    @Override
    public SessionStoreDto get(int brokerId, String clientId) {

        String key = VpaasRedisKeyUtils.getSessionKey(brokerId, clientId);

        return myRedisService.get(key, SessionStoreDto.class);
    }

    @Override
    public List<SessionStoreDto> getByUid(int brokerId, long uid) {

        String key = VpaasRedisKeyUtils.getSessionUidDeviceHashKey(brokerId, uid);

        Map<String, SessionStoreDto> map = myRedisService.hmgetAll(key, SessionStoreDto.class);

        // 返回没有过期的数据
        List<String> listToDelete = new ArrayList<>();
        Map<String, SessionStoreDto> ret = new HashMap<>();

        for (String clientId : map.keySet()) {

            SessionStoreDto sessionStoreDto = map.get(clientId);
            if (System.currentTimeMillis() - sessionStoreDto.getPingTimestamp()
                    < sessionStoreDto.getExpireSeconds() * 1000) {
                ret.put(clientId, sessionStoreDto);
            } else {
                listToDelete.add(clientId);
            }
        }

        // 删除那些过期的数据
        if (listToDelete.size() > 0) {
            String[] array = listToDelete.toArray(new String[0]);
            myRedisService.hdel(key, array);
        }

        // sort 返回 最新的排序
        return map.values()
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getPingTimestamp(), e1.getPingTimestamp()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean containsKey(int brokerId, String clientId) {

        String key = VpaasRedisKeyUtils.getSessionKey(brokerId, clientId);

        return myRedisService.exist(key);
    }

    @Override
    public void remove(int brokerId, String clientId) {

        SessionStoreDto sessionStoreDto = get(brokerId, clientId);
        if (sessionStoreDto != null) {

            String key = VpaasRedisKeyUtils.getSessionKey(brokerId, clientId);
            myRedisService.del(key);

            // 删除用户的client数据
            if (sessionStoreDto.getSessionStoreKeyDto().getUid() != VpaasConstants.DEFAULT_ERROR_UID) {

                key = VpaasRedisKeyUtils.getSessionUidDeviceHashKey(brokerId,
                        sessionStoreDto.getSessionStoreKeyDto().getUid());
                myRedisService.hdel(key, clientId);
            }
        }
    }

    @Override
    public void removeUser(int brokerId, String clientId, long uid) {

        String key = VpaasRedisKeyUtils.getSessionUidDeviceHashKey(brokerId, uid);
        myRedisService.hdel(key, clientId);
    }

    // 一个用户支持多客户端登录
    protected void putUser(int brokerId, String clientId, long uid, SessionStoreDto sessionStoreDto) {

        // 由于不同的device的过期时间不一样，这里不对 直接设置expire,用一个比较大的时间，比如1小时
        // 加入hashmap, 会删除，半小时后过期
        String key = VpaasRedisKeyUtils.getSessionUidDeviceHashKey(brokerId, uid);
        myRedisService.hset(key, clientId, sessionStoreDto, VpaasRedisKeyUtils.uidSessionExpireSeconds);
    }

}
