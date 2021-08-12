package com.github.knightliao.vpaas.common.store.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.knightliao.middle.redis.IMyRedisBatchService;
import com.github.knightliao.middle.utils.trans.JsonUtils;
import com.github.knightliao.vpaas.common.basic.constants.VpaasConstants;
import com.github.knightliao.vpaas.common.store.ISessionStoreBatchService;
import com.github.knightliao.vpaas.common.store.dto.SessionStoreDto;
import com.github.knightliao.vpaas.common.store.support.VpaasRedisKeyUtils;

/**
 * @author knightliao
 * @date 2021/8/12 10:47
 */
public class SessionStoreBatchServiceImpl implements ISessionStoreBatchService {

    private IMyRedisBatchService myRedisBatchService;

    public SessionStoreBatchServiceImpl(IMyRedisBatchService myRedisBatchService) {
        this.myRedisBatchService = myRedisBatchService;
    }

    @Override
    public Map<String, SessionStoreDto> getClients(int brokerId, List<String> clientIds) {

        List<String> keyList = new ArrayList<>();
        for (String client : clientIds) {
            keyList.add(VpaasRedisKeyUtils.getSessionKey(brokerId, client));
        }

        Map<String, String> map = myRedisBatchService.batchGetData(keyList);
        Map<String, SessionStoreDto> resultMap = new HashMap<>();

        for (String key : map.keySet()) {

            SessionStoreDto sessionStoreDto = JsonUtils.fromJson(map.get(key), SessionStoreDto.class);
            resultMap.put(sessionStoreDto.getSessionStoreKeyDto().getClientId(), sessionStoreDto);
        }

        return resultMap;
    }

    @Override
    public Map<Long, List<SessionStoreDto>> getByUids(int brokerId, List<Long> uids) {

        List<String> keyList = new ArrayList<>();
        for (Long uid : uids) {
            keyList.add(VpaasRedisKeyUtils.getSessionUidDeviceHashKey(brokerId, uid));
        }

        Map<String, String> map = myRedisBatchService.batchGetData(keyList);

        Map<Long, List<SessionStoreDto>> resultMap = new HashMap<>();

        for (String key : map.keySet()) {

            String data = map.get(key);
            SessionStoreDto sessionStoreDto = JsonUtils.fromJson(data, SessionStoreDto.class);
            SessionStoreDto.SessionStoreKeyDto sessionStoreKeyDto = sessionStoreDto.getSessionStoreKeyDto();

            if (sessionStoreKeyDto == null) {
                continue;
            }

            long uid = sessionStoreKeyDto.getUid();
            if (uid == VpaasConstants.DEFAULT_ERROR_UID) {
                continue;
            }

            List<SessionStoreDto> list = new ArrayList<>();
            if (resultMap.containsKey(uid)) {
                list = resultMap.get(uid);
            }

            // 这里不删除过期的数据，但是返回 会进行过滤
            if (System.currentTimeMillis() - sessionStoreDto.getPingTimestamp()
                    < sessionStoreDto.getExpireSeconds() * 1000) {
                list.add(sessionStoreDto);
            } else {
                //
            }

            resultMap.put(uid, list);
        }

        return resultMap;
    }
}
