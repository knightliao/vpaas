package com.github.knightliao.vpaas.lc.server.start.controller.status;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.knightliao.middle.api.core.dto.MyBaseResponse;
import com.github.knightliao.middle.web.aop.QpsAnnotation;
import com.github.knightliao.vpaas.api.web.request.VpaasOfflineRequest;
import com.github.knightliao.vpaas.api.web.response.VpaasOfflineResponse;
import com.github.knightliao.vpaas.lc.server.router.facade.VpaasStatusOpFacade;

import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/20 00:42
 */
@Slf4j
@RestController
@RequestMapping(value = "/v1/vpaas/lc/status", produces = MediaType.APPLICATION_JSON_VALUE,
        method = {RequestMethod.POST, RequestMethod.GET})
public class VpaasLcStatusController {

    @Resource
    private VpaasStatusOpFacade vpaasStatusOpFacade;

    @QpsAnnotation(printParam = true)
    @RequestMapping(value = "/offline")
    public MyBaseResponse<VpaasOfflineResponse> offline(VpaasOfflineRequest vpaasOfflineRequest) {

        return vpaasStatusOpFacade.offline(vpaasOfflineRequest);
    }
}
