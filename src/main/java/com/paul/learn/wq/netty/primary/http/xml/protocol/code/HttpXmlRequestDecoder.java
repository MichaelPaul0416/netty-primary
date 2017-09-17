package com.paul.learn.wq.netty.primary.http.xml.protocol.code;

import com.paul.learn.wq.netty.primary.http.xml.bean.HttpXmlRequest;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/9
 */
public class HttpXmlRequestDecoder extends AbstractHttpXmlDecoder<FullHttpRequest> {
    private Logger logger = Logger.getLogger(HttpXmlRequestDecoder.class);

    public HttpXmlRequestDecoder(Class<?> clazz) {
        super(clazz);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest, List<Object> list) throws Exception {
        if(!fullHttpRequest.getDecoderResult().isSuccess()){//解码失败
            logger.error(String.format("HttpRequestDecoder解码失败"));
            sendError(channelHandlerContext,HttpResponseStatus.BAD_REQUEST);
            return;
        }else{
            logger.info(String.format("HttpRequestDecoder解码成功，开始xml-->Bean解码"));
            HttpXmlRequest httpXmlRequest = new HttpXmlRequest(fullHttpRequest,decode0(channelHandlerContext,fullHttpRequest.content()));
            list.add(httpXmlRequest);
        }
    }

    private static void sendError(ChannelHandlerContext context, HttpResponseStatus status){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,status,
                Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE,"text/plain; charset=utf-8");
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
