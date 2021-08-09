package com.github.knightliao.vpaas.lc.server.start.test.normal.server;

import com.alibaba.fastjson.JSONObject;
import com.github.knightliao.vpaas.common.utils.log.LoggerUtil;
import com.github.knightliao.vpaas.lc.client.IMyLcClient;
import com.github.knightliao.vpaas.lc.client.service.helper.VpaasClientFactory;
import com.github.knightliao.vpaas.lc.client.support.dto.ClientOptions;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.RequestMsg;
import com.github.knightliao.vpaas.lc.server.connect.support.dto.msg.ResponseMsg;
import com.github.knightliao.vpaas.lc.server.connect.support.enums.SocketType;
import com.github.knightliao.vpaas.lc.server.session.service.listener.json.JsonEchoMessageEventListener;

import lombok.extern.slf4j.Slf4j;

/**
 * @author knightliao
 * @date 2021/8/8 17:25
 */
@Slf4j
public class JsonClientEcho {

    public static void main(String[] args) throws Exception {

        ClientOptions clientOptions = new ClientOptions();
        clientOptions.setSocketType(SocketType.JSON);
        clientOptions.setServerList("127.0.0.1:7000");

        IMyLcClient lcClient = VpaasClientFactory.getMyLcClientImpl().newClient(clientOptions);
        lcClient.addEventListener(new JsonEchoMessageEventListener());
        lcClient.connect();

        for (int i = 0; i < 10; ++i) {

            JSONObject message = new JSONObject();
            message.put("action", "echo");
            message.put("message", "hello world!");

            RequestMsg requestMsg = new RequestMsg();
            requestMsg.setSequence(i);
            requestMsg.setMessage(message);

            try {
                ResponseMsg respone = lcClient.sendWithSync(requestMsg, 30000);
                LoggerUtil.info(log, "成功接收到同步的返回: {0}", respone);

            } catch (Exception ex) {
                log.error(ex.toString(), ex);
            }

            Thread.sleep(1000);
        }
    }
}
