package com.github.knightliao.vpaas.lc.server.router.facade.impl;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.github.knightliao.middle.api.core.callback.IMyRequesCallback;
import com.github.knightliao.middle.api.core.dto.MyBaseResponse;
import com.github.knightliao.middle.api.core.template.MyFacadeTemplate;
import com.github.knightliao.middle.lang.exceptions.BizException;
import com.github.knightliao.middle.lang.security.ParamAssertUtil;
import com.github.knightliao.vpaas.api.support.enums.VpaasLcOfflineEnum;
import com.github.knightliao.vpaas.api.web.request.VpaasOfflineRequest;
import com.github.knightliao.vpaas.api.web.response.VpaasOfflineResponse;
import com.github.knightliao.vpaas.lc.server.router.facade.VpaasStatusOpFacade;
import com.github.knightliao.vpaas.lc.server.router.service.VpaasStatusOpService;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/20 14:52
 */
@Service
public class VpaasStatusOpFacadeImpl implements VpaasStatusOpFacade {

    @Resource
    private VpaasStatusOpService vpaasStatusOpService;

    @Override
    public MyBaseResponse<VpaasOfflineResponse> offline(VpaasOfflineRequest vpaasOfflineRequest) {

        return MyFacadeTemplate.execute(vpaasOfflineRequest, new IMyRequesCallback<VpaasOfflineResponse>() {

            @Override
            public void checkParams() throws BizException {

                ParamAssertUtil.assertArgumentValid(!StringUtils.isEmpty(vpaasOfflineRequest.getClientId()),
                        "clientId");
                ParamAssertUtil.assertArgumentValid(!StringUtils.isEmpty(vpaasOfflineRequest.getFrom()), "from");
            }

            @Override
            public MyBaseResponse<VpaasOfflineResponse> process() {

                VpaasLcOfflineEnum vpaasLcOfflineEnum = vpaasStatusOpService.offline(vpaasOfflineRequest);

                return MyBaseResponse.success(getResult(vpaasLcOfflineEnum));
            }

        });
    }

    private VpaasOfflineResponse getResult(VpaasLcOfflineEnum vpaasLcOfflineEnum) {

        VpaasOfflineResponse vpaasOfflineResponse =
                VpaasOfflineResponse.builder().vpaasLcOfflineEnum(vpaasLcOfflineEnum).build();

        vpaasOfflineResponse.setInfo(vpaasLcOfflineEnum.getDesc());
        vpaasOfflineResponse.setStatusCode(vpaasLcOfflineEnum.getValue());
        return vpaasOfflineResponse;
    }
}
