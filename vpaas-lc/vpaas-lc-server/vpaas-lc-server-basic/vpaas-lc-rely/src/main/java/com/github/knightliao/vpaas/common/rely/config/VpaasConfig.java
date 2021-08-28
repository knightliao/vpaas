package com.github.knightliao.vpaas.common.rely.config;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.github.knightliao.middle.utils.lang.MyStringUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/18 15:56
 */
@EnableConfigurationProperties
@PropertySource("classpath:vpaas.properties")
@Slf4j
@Configuration
@EnableCaching
@Data
public class VpaasConfig {

    @Value("${vpaas.lc.server.brokerId:0}")
    private int serverBrokerId;

    @Value("${vpaas.lc.server.port:6000}")
    private int serverPort;

    @Value("${vpaas.lc.server.status.port:6001}")
    private int serverStatusPort;

    @Value("${vpaas.lc.server.log.middleware.debug:false}")
    private boolean logMiddlewareDebug;

    @Value("${vpaas.lc.server.log.web.request.debug:false}")
    private boolean webRequestLogDebug;

    @Value("${vpaas.lc.server.log.clients}")
    private String logClients;

    @Value("${vpaas.lc.server.log.uids")
    private String logUids;

    public Set<String> getLogClientSet() {

        try {
            return new HashSet<>(MyStringUtils.split(logClients, ","));
        } catch (Exception ex) {
            return new HashSet<>();
        }
    }

    public Set<Long> getLogUidSet() {

        try {
            return new HashSet<>(MyStringUtils.splitToLong(logUids, ","));
        } catch (Exception ex) {
            return new HashSet<>();
        }
    }

    public boolean isPrintLog(Long uid) {
        if (uid == null) {
            return false;
        }

        return this.getLogUidSet().contains(uid);
    }

    public boolean isPrintLog(String clientId) {

        if (StringUtils.isEmpty(clientId)) {
            return false;
        }

        return getLogClientSet().contains(clientId);
    }

    public boolean isPrintLog(String clientId, long uid) {

        return isPrintLog(clientId) || isPrintLog(uid);
    }
}