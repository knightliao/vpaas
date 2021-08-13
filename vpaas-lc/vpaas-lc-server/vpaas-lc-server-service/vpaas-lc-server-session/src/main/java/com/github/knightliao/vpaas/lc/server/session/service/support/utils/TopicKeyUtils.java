package com.github.knightliao.vpaas.lc.server.session.service.support.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author knightliao
 * @email knightliao@gmail.com
 * @date 2021/8/13 16:26
 */
public class TopicKeyUtils {

    public static String getP2pTargetTopic(String clientId){

        return "";
    }

    public static String getClientIdFromP2pTopic(String topic){

        if (StringUtils.isEmpty(topic)) {
            return null;
        }

        String [] strList = StringUtils.split(topic,"/");
        if(strList.length !=3){
            return null;
        }

        return strList[2];
    }
}
