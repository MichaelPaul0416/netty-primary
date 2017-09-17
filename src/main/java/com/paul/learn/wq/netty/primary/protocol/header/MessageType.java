package com.paul.learn.wq.netty.primary.protocol.header;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/16
 */
public interface MessageType {

    byte SERVICE_REQUEST  = 0;//业务请求消息

    byte SERVICE_REPONSE = 1;//业务相应消息

    byte SERVICE_REQUEST_RESPONSE = 2;//既是请求消息，又是相应消息

    byte HANDS_REQUEST = 3;//握手请求消息

    byte HANDS_RESPONSE = 4;//握手应答消息

    byte HEART_REQUEST = 5;//心跳请求消息

    byte HEART_RESPONSE = 6;//心跳应答消息

}
