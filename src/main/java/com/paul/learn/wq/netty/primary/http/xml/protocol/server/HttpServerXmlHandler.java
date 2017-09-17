package com.paul.learn.wq.netty.primary.http.xml.protocol.server;

import com.paul.learn.wq.netty.primary.http.xml.bean.HttpXmlRequest;
import com.paul.learn.wq.netty.primary.http.xml.bean.HttpXmlResponse;
import com.paul.learn.wq.netty.primary.http.xml.bean.Order;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.log4j.Logger;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/9
 */
public class HttpServerXmlHandler extends SimpleChannelInboundHandler<HttpXmlRequest>{

    private Logger logger = Logger.getLogger(HttpServerXmlHandler.class);

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, HttpXmlRequest httpXmlRequest) throws Exception {

        Object object = httpXmlRequest.getBody();
        logger.info(String.format("Handler中收到对象【%s】",object));
        Order order = (Order) object;
        order.setOrderNumber(2);
        logger.info(String.format("返回Order对象【%s】",order));
        ChannelFuture future = channelHandlerContext.writeAndFlush(new HttpXmlResponse(null,order));
        if(HttpHeaders.isKeepAlive(httpXmlRequest.getRequest())){
            future.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    logger.info(String.format("与客户端的通道【%s】关闭",channelHandlerContext.channel().remoteAddress()));
                    channelHandlerContext.close();
                }
            });
        }
    }

    private static void sendError(ChannelHandlerContext context, HttpResponseStatus status){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,status,
                Unpooled.copiedBuffer("失败: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE,"text/plain; charset=utf-8");
        context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context,Throwable e) throws Exception{
        logger.error(String.format("服务端出错【%s】",e));
//        e.printStackTrace();
    }
}
