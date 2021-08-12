package com.github.knightliao.vpaas.lc.client.service.helper;

import com.github.knightliao.vpaas.lc.client.IMyLcClient;
import com.github.knightliao.vpaas.lc.client.service.impl.MyLcClientImpl;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/9 11:40
 */
public class VpaasClientFactory {

    public static IMyLcClient getMyLcClientImpl() {
        return new MyLcClientImpl();
    }
}
