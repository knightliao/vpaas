package com.github.knightliao.vpaas.lc.server.server.listener.status.helper;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.github.knightliao.vpaas.lc.server.connect.netty.server.LcServerContext;
import com.github.knightliao.vpaas.lc.server.connect.netty.service.ILcService;
import com.github.knightliao.vpaas.lc.server.connect.netty.statistics.dto.LcCountInfoDto;
import com.github.knightliao.vpaas.lc.server.server.service.IMyLcServer;

/**
 * @author knightliao
 * @date 2021/8/8 00:23
 */
public class LcServerStatusHelper {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static String formatResultMap(Map<String, Object> resultMap) {

        if (resultMap == null || resultMap.size() <= 0) {
            return StringUtils.EMPTY;
        }

        StringBuffer sb = new StringBuffer();
        sb.append("{").append(LINE_SEPARATOR);
        for (Map.Entry<String, Object> row : resultMap.entrySet()) {
            sb.append(String.format(" %-25s", row.getKey())).append("\t").append(row.getValue()).append(LINE_SEPARATOR);
        }
        sb.append("}").append(LINE_SEPARATOR);

        return sb.toString();
    }

    public static Map<String, Object> doGetStatus() {

        Map<String, Object> resultMap = new LinkedHashMap<>();
        IMyLcServer server = (IMyLcServer) LcServerContext.getContext().getServer();
        ILcService service = (ILcService) server;

        if (server != null) {

            // 名称
            resultMap.put("getBrokerId", server.getServerParam().getBrokerId());
            resultMap.put("getBrokerName", server.getServerParam().getBrokerName());
            resultMap.put("getWebSocketPath", server.getServerParam().getWebSocketPath());
            resultMap.put("getMqttVersion", server.getServerParam().getMqttVersion());

            // server启动时间
            resultMap.put("getStartTime", server.getServerParam().getStartTime());

            // 连接数
            resultMap.put("getChannels().size()", server.getChannels().size());
            resultMap.put("getChannelGroup().size()", server.getChannelGroup().size());

            //
            LcCountInfoDto lcCountInfoDto = service.getLcServiceParam().getCountInfoDto();

            // 最大连接数
            resultMap.put("getMaxChannelNum()", lcCountInfoDto.getMaxChannelNum());

            // 最后接收消息时间
            if (lcCountInfoDto.getLastReceiveTimeStamp() > 0) {
                resultMap.put("getLastReceiveTimeStamp()", lcCountInfoDto.getLastReceiveTimeStamp());
            } else {
                resultMap.put("getLastReceiveTimeStamp()", 0);
            }
        }

        return resultMap;
    }
}
