package com.github.knightliao.vpaas.lc.server.connect.netty.statistics.service.impl;

import com.github.knightliao.vpaas.lc.server.connect.netty.server.ILcServer;
import com.github.knightliao.vpaas.lc.server.connect.netty.server.LcServerContext;
import com.github.knightliao.vpaas.lc.server.connect.netty.service.ILcService;
import com.github.knightliao.vpaas.lc.server.connect.netty.statistics.dto.LcCountInfoDto;
import com.github.knightliao.vpaas.lc.server.connect.netty.statistics.service.ILcCounterService;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.param.LcServiceParam;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/4 20:12
 */
public class LcCounterServiceImpl implements ILcCounterService {

    @Override
    public void incrSend() {

        if (isCountStatistic()) {

            // get
            LcCountInfoDto lcCountInfoDto = getLcCountInfoDto();

            // incr
            lcCountInfoDto.getSentNum().incrementAndGet();

            // set last send time
            lcCountInfoDto.setLastSentTimeStamp(System.currentTimeMillis());
        }
    }

    @Override
    public void incrReceive() {

        if (isCountStatistic()) {

            // get
            LcCountInfoDto lcCountInfoDto = getLcCountInfoDto();

            // incr
            lcCountInfoDto.getReceiveNum().incrementAndGet();

            // set last send time
            lcCountInfoDto.setLastReceiveTimeStamp(System.currentTimeMillis());
        }

    }

    @Override
    public void setMaxChannelNum(long channelNum) {

        getLcCountInfoDto().setMaxChannelNum(channelNum);
    }

    @Override
    public void incrHeartbeatNum() {

        if (isCountStatistic()) {

            // get
            LcCountInfoDto lcCountInfoDto = getLcCountInfoDto();

            // incr
            lcCountInfoDto.getHeartbeatNum().incrementAndGet();
        }
    }

    @Override
    public boolean isCountStatistic() {
        ILcServer lcServer = LcServerContext.getContext().getServer();
        if (lcServer != null) {

            ILcService lcService = (ILcService) lcServer;
            LcServiceParam serviceParam = lcService.getLcServiceParam();
            if (serviceParam.isOpenCount()) {
                return true;
            }
        }

        return false;
    }

    private LcCountInfoDto getLcCountInfoDto() {

        ILcServer lcServer = LcServerContext.getContext().getServer();
        ILcService lcService = (ILcService) lcServer;

        LcServiceParam serviceParam = lcService.getLcServiceParam();
        return serviceParam.getCountInfoDto();
    }
}
