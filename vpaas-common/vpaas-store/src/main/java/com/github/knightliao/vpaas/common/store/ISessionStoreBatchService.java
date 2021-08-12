package com.github.knightliao.vpaas.common.store;

import java.util.List;
import java.util.Map;

import com.github.knightliao.vpaas.common.store.dto.SessionStoreDto;

/**
 * @author knightliao
 * @date 2021/8/12 10:46
 */
public interface ISessionStoreBatchService {

    // 获取会话
    Map<String, SessionStoreDto> getClients(int brokerId, List<String> clientIds);

    // 获取用户会话
    Map<Long, List<SessionStoreDto>> getByUids(int brokerId, List<Long> uids);
}
