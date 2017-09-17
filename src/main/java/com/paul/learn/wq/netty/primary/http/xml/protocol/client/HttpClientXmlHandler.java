package com.paul.learn.wq.netty.primary.http.xml.protocol.client;

import com.paul.learn.wq.netty.primary.http.xml.bean.HttpXmlRequest;
import com.paul.learn.wq.netty.primary.http.xml.bean.HttpXmlResponse;
import com.paul.learn.wq.netty.primary.http.xml.bean.Order;
import com.paul.learn.wq.netty.primary.http.xml.j2butil.XmlBeanUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import org.apache.log4j.Logger;


/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/9
 */
public class HttpClientXmlHandler extends SimpleChannelInboundHandler<HttpXmlResponse> {

    private Logger logger = Logger.getLogger(HttpClientXmlHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Order order = XmlBeanUtil.instanceOrder();
        logger.info(String.format("Order【%s】",order));
        HttpXmlRequest request = new HttpXmlRequest(null,order);
        ctx.writeAndFlush(request);
        logger.info("发送完毕");
    }


    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, HttpXmlResponse httpXmlResponse) throws Exception {
        Object order = httpXmlResponse.getObject();
        FullHttpResponse response = httpXmlResponse.getResponse();

        logger.info(String.format("header【%s】",response.headers().names()));

        logger.info(String.format("body【%s】",order));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context,Throwable e) throws Exception{
        logger.error(String.format("客户端出错【%s】",e));
//        e.printStackTrace();
    }
}
