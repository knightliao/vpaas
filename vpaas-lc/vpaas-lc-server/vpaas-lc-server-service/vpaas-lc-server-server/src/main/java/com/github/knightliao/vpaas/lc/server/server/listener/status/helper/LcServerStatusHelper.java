package com.github.knightliao.vpaas.lc.server.server.listener.status.helper;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.github.knightliao.vpaas.lc.server.connect.netty.server.LcServerContext;
import com.github.knightliao.vpaas.lc.server.connect.netty.service.ILcService;
import com.github.knightliao.vpaas.lc.server.connect.netty.statistics.dto.LcCountInfoDto;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.ExecutorEnum;
import com.github.knightliao.vpaas.lc.server.server.IMyLcServer;

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

    public static Map<String, Object> doGetExecutors() {

        Map<String, Object> resultMap = new LinkedHashMap<>();
        IMyLcServer server = (IMyLcServer) LcServerContext.getContext().getServer();
        ILcService service = (ILcService) server;

        if (server != null) {

            // 名称
            resultMap.put("getBrokerId", server.getServerParam().getBrokerId());
            resultMap.put("getBrokerName", server.getServerParam().getBrokerName());

            for (ExecutorEnum executorEnum : ExecutorEnum.values()) {

                resultMap.put(executorEnum.getDesc() + "_getExecutorActiveCount_",
                        service.getLcServiceExecutorMgr().getExecutorActiveCount(executorEnum));

                resultMap.put(executorEnum.getDesc() + "_getExecutorCompletedTaskCount_",
                        service.getLcServiceExecutorMgr().getExecutorCompletedTaskCount(executorEnum));

                resultMap.put(executorEnum.getDesc() + "_getExecutorLargestPoolSize_",
                        service.getLcServiceExecutorMgr().getExecutorLargestPoolSize(executorEnum));

                resultMap.put(executorEnum.getDesc() + "_getExecutorPoolSize_",
                        service.getLcServiceExecutorMgr().getExecutorPoolSize(executorEnum));

                resultMap.put(executorEnum.getDesc() + "_getExecutorQueueSize_",
                        service.getLcServiceExecutorMgr().getExecutorQueueSize(executorEnum));

                resultMap.put(executorEnum.getDesc() + "_getExecutorTaskCount_",
                        service.getLcServiceExecutorMgr().getExecutorTaskCount(executorEnum));
            }
        }

        return resultMap;
    }

    public static Map<String, Object> doGetConfig() {

        Map<String, Object> resultMap = new LinkedHashMap<>();
        IMyLcServer server = (IMyLcServer) LcServerContext.getContext().getServer();
        ILcService service = (ILcService) server;

        if (server != null) {

            // ip
            resultMap.put("getIp",
                    service.getLcServiceParam().getIp() == null ? "" : service.getLcServiceParam().getIp());

            // port
            resultMap.put("getPort", service.getLcServiceParam().getPort());

            resultMap.put("getSocketType", service.getLcServiceParam().getSocketType());

            resultMap.put("isKeepAlive", service.getLcServiceParam().isKeepAlive());

            resultMap.put("isTcpNoDelay", service.getLcServiceParam().isTcpNoDelay());

            resultMap.put("getWorkerCount", service.getLcServiceParam().getWorkerCount());

            resultMap.put("getBossCount", service.getLcServiceParam().getBossCount());

            //
            resultMap.put("isOpenExecutor", service.getLcServiceParam().isOpenExecutor());
            if (service.getLcServiceParam().isOpenExecutor()) {

                resultMap.put("getCorePoolSize", service.getLcServiceParam().getCorePoolSize());
                resultMap.put("getMaximumPoolSize", service.getLcServiceParam().getMaximumPoolSize());
                resultMap.put("getQueueCapacity", service.getLcServiceParam().getQueueCapacity());

                resultMap.put("getCorePoolChannelSize", service.getLcServiceParam().getCorePoolChannelSize());
                resultMap.put("getMaximumPoolChannelSize", service.getLcServiceParam().getMaximumPoolChannelSize());
                resultMap.put("getQueueChannelCapacity", service.getLcServiceParam().getQueueChannelCapacity());

                resultMap.put("getCorePoolExceptionSize", service.getLcServiceParam().getCorePoolExceptionSize());
                resultMap.put("getMaximumPoolExceptionSize", service.getLcServiceParam().getMaximumPoolExceptionSize());
                resultMap.put("getQueueExceptionCapacity", service.getLcServiceParam().getQueueExceptionCapacity());
            }

            //
            resultMap.put("isCheckHeartbeat", service.getLcServiceParam().isCheckHeartbeat());
            if (service.getLcServiceParam().isCheckHeartbeat()) {

                resultMap.put("getReadIdleTimeSeconds", service.getLcServiceParam().getReadIdleTimeSeconds());
                resultMap.put("getWriteIdleTimeSeconds", service.getLcServiceParam().getWriteIdleTimeSeconds());
                resultMap.put("getAllIdleTimeSeconds", service.getLcServiceParam().getAllIdleTimeSeconds());
            }
        }

        return resultMap;
    }
}
