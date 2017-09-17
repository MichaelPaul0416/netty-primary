package com.paul.learn.wq.netty.primary.http.xml.bean;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/9
 */
public class HttpXmlResponse {
    private FullHttpResponse response;

    private Object object;

    public HttpXmlResponse(){}

    public HttpXmlResponse(FullHttpResponse response, Object order){
        this.response = response;
        this.object = order;
    }
    public FullHttpResponse getResponse() {
        return response;
    }

    public void setResponse(FullHttpResponse response) {
        this.response = response;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object order) {
        this.object = order;
    }
}
