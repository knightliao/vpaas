package com.github.knightliao.vpaas.common.store.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.knightliao.middle.lang.constants.PackConstants;
import com.github.knightliao.middle.redis.IMyRedisService;
import com.github.knightliao.vpaas.common.store.ISessionStoreService;
import com.github.knightliao.vpaas.common.store.dto.SessionStoreDto;
import com.github.knightliao.vpaas.common.store.support.VpaasRedisKeyUtils;

/**
 * @author knightliao
 * @email knightliao@gmail.com
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

        if (sessionStoreKeyDto.getUid() != PackConstants.DEFAULT_ERROR_UID) {
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

        // ???????????????????????????
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

        // ???????????????????????????
        if (listToDelete.size() > 0) {
            String[] array = listToDelete.toArray(new String[0]);
            myRedisService.hdel(key, array);
        }

        // sort ?????? ???????????????
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

            // ???????????????client??????
            if (sessionStoreDto.getSessionStoreKeyDto().getUid() != PackConstants.DEFAULT_ERROR_UID) {

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

    // ????????????????????????????????????
    protected void putUser(int brokerId, String clientId, long uid, SessionStoreDto sessionStoreDto) {

        // ???????????????device??????????????????????????????????????? ????????????expire,????????????????????????????????????1??????
        // ??????hashmap, ??????????????????????????????
        String key = VpaasRedisKeyUtils.getSessionUidDeviceHashKey(brokerId, uid);
        myRedisService.hset(key, clientId, sessionStoreDto, VpaasRedisKeyUtils.uidSessionExpireSeconds);
    }

}
