package com.github.knightliao.vpaas.lc.server.start.service;

import java.util.Map;

import com.github.knightliao.vpaas.lc.server.connect.netty.server.ILcServer;
import com.github.knightliao.vpaas.lc.server.start.support.dto.ServerOptionsDto;

import io.netty.channel.ChannelHandler;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/7 17:25
 */
public interface IServerNewService {

    ILcServer newServer(ServerOptionsDto serverOptionsDto, Map<String, ChannelHandler> mapHandlers);

    ILcServer newServer(ServerOptionsDto serverOptionsDto);
}
