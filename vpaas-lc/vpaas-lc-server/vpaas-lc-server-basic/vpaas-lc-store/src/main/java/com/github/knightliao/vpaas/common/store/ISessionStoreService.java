package com.github.knightliao.vpaas.common.store;

import java.util.List;

import com.github.knightliao.vpaas.common.store.dto.SessionStoreDto;

/**
 * 会话存储服务
 *
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/11 14:49
 */
public interface ISessionStoreService {

    // 存储会话
    void put(SessionStoreDto sessionStoreDto, int expire);

    // 获取会话
    SessionStoreDto get(int brokerId, String clientId);

    // 获取会话
    List<SessionStoreDto> getByUid(int brokerId, long uid);

    // clientId的会话是否存在
    boolean containsKey(int brokerId, String clientId);

    // 删除会话
    void remove(int brokerId, String clientId);

    // 删除用户会话
    void removeUser(int brokerId, String clientId, long uid);
}
