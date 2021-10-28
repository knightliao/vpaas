package com.github.knightliao.vpaas.demos.demo.demo.mess.helper;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import com.github.knightliao.middle.idgen.IIdgenService;
import com.github.knightliao.middle.utils.net.IpUtils;
import com.github.knightliao.vpaas.demos.demo.config.VpaasBenchmarkConfig;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/10/28 21:45
 */
@Service
public class VpaasMultiDemoHelper {

    @Resource
    private IIdgenService idgenService;

    @Resource
    private VpaasBenchmarkConfig vpaasBenchmarkConfig;

    private String ip;

    public VpaasMultiDemoHelper() {
        ip = IpUtils.getLocalIp();
    }

    public int getInstanceSize() {
        ImmutablePair<Integer, Integer> ret = vpaasBenchmarkConfig.getSizeInfo();
        return ret.getLeft() / ret.getRight();
    }

    public ImmutablePair<String, Long> getClientAndUid(long index) {

        Long uid = getUid();
        String clientId = getClient(index);

        return ImmutablePair.of(clientId, uid);
    }

    private Long getUid() {

        return idgenService.getSequenceId("vmate_client_demo");
    }

    private String getClient(long index) {

        return String.format("GID_XXX@@%s_%d_", ip, index);
    }
}
