package com.paul.learn.wq.netty.primary.http.xml.protocol.code;

import com.paul.learn.wq.netty.primary.http.xml.bean.HttpXmlRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.util.List;

/**
 * @Author:wangqiang20995
 * @description:这个类本质上其实还是编码器，不是handler
 * @Date:2017/9/9
 */
public class HttpXmlRequestEncoder extends AbstractHttpXmlEncoder<HttpXmlRequest> {

    private Logger logger = Logger.getLogger(HttpXmlRequestEncoder.class);


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, HttpXmlRequest httpXmlRequest, List<Object> list) throws Exception {

        ByteBuf byteBuf = super.encode0(channelHandlerContext,httpXmlRequest.getBody());//调用父类方法，获取字节码
        FullHttpRequest request = httpXmlRequest.getRequest();

        logger.info(String.format("为管道【%s】中的http设置请求头信息",channelHandlerContext.channel().remoteAddress()));
        if(request == null){
            logger.info(String.format("构造请求头相对应的信息"));
            request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,"/do",byteBuf);//设置http版本号以及请求方式和uri，并且加入对应的请求消息
            HttpHeaders httpHeaders = request.headers();
            //设置请求头
            httpHeaders.set(HttpHeaders.Names.HOST, InetAddress.getLocalHost().getHostAddress());
            httpHeaders.set(HttpHeaders.Names.CONNECTION,HttpHeaders.Values.CLOSE);
            httpHeaders.set(HttpHeaders.Names.ACCEPT_ENCODING,HttpHeaders.Values.GZIP.toString()+','+HttpHeaders.Values.DEFLATE.toString());
            httpHeaders.set(HttpHeaders.Names.ACCEPT_CHARSET,"ISO-8859-1,utf-8;q=0.7,*;q=0.7");
            httpHeaders.set(HttpHeaders.Names.ACCEPT_LANGUAGE,"zh");
            httpHeaders.set(HttpHeaders.Names.USER_AGENT,"Netty xml Http Client side");
            httpHeaders.set(HttpHeaders.Names.ACCEPT,"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        }
        HttpHeaders.setContentLength(request,byteBuf.readableBytes());
        logger.info(String.format("请求头构造完毕"));
        list.add(request);
    }
}
