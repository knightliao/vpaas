package com.github.knightliao.vpaas.monitor.controller.lc.status;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.knightliao.middle.web.aop.QpsAnnotation;
import com.github.knightliao.vpaas.common.rely.config.VpaasConfig;
import com.github.knightliao.vpaas.common.store.ISessionStoreService;
import com.github.knightliao.vpaas.common.store.ITokenStoreService;
import com.github.knightliao.vpaas.common.store.dto.SessionStoreDto;
import com.github.knightliao.vpaas.monitor.support.constants.VpaasMonitorConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/29 01:04
 */
@Slf4j
@Controller
@RequestMapping("/vpaas-monitor/lc")
public class VpaasMonitorLcStatusController {

    @Resource
    private VpaasConfig vpaasConfig;

    @Resource
    private ISessionStoreService sessionStoreService;

    @Resource
    private ITokenStoreService tokenStoreService;

    @QpsAnnotation(printParam = true)
    @RequestMapping(value = "/status", method = {RequestMethod.GET})
    public String status(Long uid, String clientId, ModelMap modelMap) {

        SessionStoreDto sessionStoreDtoClient = null;
        if (!StringUtils.isEmpty(clientId)) {
            sessionStoreDtoClient = sessionStoreService.get(vpaasConfig.getServerBrokerId(), clientId);
        }

        List<SessionStoreDto> sessionStoreDtoListUid = new ArrayList<>();
        if (uid != null) {
            sessionStoreDtoListUid = sessionStoreService.getByUid(vpaasConfig.getServerBrokerId(), uid);
        }

        String token = null;
        if (!StringUtils.isEmpty(clientId)) {
            token = tokenStoreService.getToken(vpaasConfig.getServerBrokerId(), clientId);
        }

        modelMap.addAttribute("sessionStoreDtoClient", sessionStoreDtoClient);
        modelMap.addAttribute("sessionStoreDtoListUid", sessionStoreDtoListUid);
        modelMap.addAttribute("token", token);

        return VpaasMonitorConstants.LC_STATUS;
    }
}
