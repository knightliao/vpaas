package com.github.knightliao.vpaas.lc.monitor.controller.index;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/28 19:18
 */
@Slf4j
@Controller
@RequestMapping("")
public class VpaasMonitorIndexController {

    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public String index(ModelMap modelMap) {

        return "index";
    }
}
