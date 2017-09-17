package com.paul.learn.wq.netty.primary.http.xml.bean;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/9
 */
public class HttpXmlRequest {

    private FullHttpRequest request;

    private Object body;

    public HttpXmlRequest(){}

    public HttpXmlRequest(FullHttpRequest request,Object body) {
        this.request = request;
        this.body = body;
    }

    public FullHttpRequest getRequest() {
        return request;
    }

    public void setRequest(FullHttpRequest request) {
        this.request = request;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
