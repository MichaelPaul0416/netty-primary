package com.paul.learn.wq.netty.primary.protocol.header;

/**
 * @Author:wangqiang20995
 * @description:作为网络传输的bean，类似于netty的http协议栈中的HttPRequest/HttpResponse
 * @Date:2017/9/16
 */
public class NettyMessage {
    private NettyHeader header;

    private Object body ;

    @Override
    public String toString() {
        return "NettyMessage{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public NettyHeader getHeader() {

        return header;
    }

    public void setHeader(NettyHeader header) {
        this.header = header;
    }
}
