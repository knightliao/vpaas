package com.github.knightliao.vpaas.lc.server.connect.netty.handler;

import com.github.knightliao.vpaas.lc.server.connect.netty.server.ILcServer;
import com.github.knightliao.vpaas.lc.server.connect.netty.server.LcServerContext;
import com.github.knightliao.vpaas.lc.server.connect.netty.statistics.service.LcCounterServiceFactory;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 一些核心的统计
 *
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/5 21:25
 */
@ChannelHandler.Sharable
public class LcCountHandler extends ChannelInboundHandlerAdapter {

    // 记录 当前最大通道个数，以及记录历史最大个数
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        ILcServer server = LcServerContext.getContext().getServer();
        LcCounterServiceFactory.getILcCounterService().setMaxChannelNum(server.incrChannelMaxSize(true));

        super.channelActive(ctx);
    }

    // 记录 当前通道个数 以及记录历史最大个数
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        ILcServer server = LcServerContext.getContext().getServer();
        LcCounterServiceFactory.getILcCounterService().setMaxChannelNum(server.incrChannelMaxSize(false));

        super.channelInactive(ctx);
    }

    // 记录 收到的消息个数 以及时间
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        LcCounterServiceFactory.getILcCounterService().incrReceive();

        super.channelRead(ctx, msg);
    }

}
