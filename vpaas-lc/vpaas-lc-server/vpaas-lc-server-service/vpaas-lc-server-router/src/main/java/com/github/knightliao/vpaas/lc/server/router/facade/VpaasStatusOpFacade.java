package com.github.knightliao.vpaas.lc.server.router.facade;

import com.github.knightliao.middle.api.core.dto.MyBaseResponse;
import com.github.knightliao.vpaas.api.web.request.VpaasOfflineRequest;
import com.github.knightliao.vpaas.api.web.response.VpaasOfflineResponse;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/20 14:52
 */
public interface VpaasStatusOpFacade {

    MyBaseResponse<VpaasOfflineResponse> offline(VpaasOfflineRequest vpaasOfflineRequest);


}
