package com.paul.learn.wq.netty.primary.http.xml.protocol.code;

import com.paul.learn.wq.netty.primary.http.xml.bean.HttpXmlResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.List;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/9
 */
public class HttpXmlResponseDecoder extends AbstractHttpXmlDecoder<FullHttpResponse> {

    public HttpXmlResponseDecoder(Class<?> clazz) {
        super(clazz);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, FullHttpResponse response, List<Object> list) throws Exception {
        Object object = decode0(channelHandlerContext,response.content());
        HttpXmlResponse httpXmlResponse = new HttpXmlResponse(response,object);
        list.add(httpXmlResponse);
    }
}
