package com.github.knightliao.vpaas.lc.server.router.service;

import com.github.knightliao.vpaas.api.support.enums.VpaasLcOfflineEnum;
import com.github.knightliao.vpaas.api.web.request.VpaasOfflineRequest;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/22 15:20
 */
public interface VpaasStatusOpService {

    VpaasLcOfflineEnum offline(VpaasOfflineRequest vpaasOfflineRequest);
}
