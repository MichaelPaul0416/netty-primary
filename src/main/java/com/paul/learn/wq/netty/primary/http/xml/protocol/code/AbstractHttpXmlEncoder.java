package com.paul.learn.wq.netty.primary.http.xml.protocol.code;

import com.paul.learn.wq.netty.primary.http.xml.j2butil.XmlBeanUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/9
 */
public abstract class AbstractHttpXmlEncoder<T> extends MessageToMessageEncoder<T>{

    private Logger logger = Logger.getLogger(AbstractHttpXmlEncoder.class);

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    protected ByteBuf encode0(ChannelHandlerContext context,Object object) throws Exception{

        logger.info(String.format("HTTP编码器收到客户端【%s】发来的请求",context.channel().remoteAddress()));
        logger.info(String.format("开始序列化bean【%s】",object.getClass().getName()));
        String request = XmlBeanUtil.convertBean2Xml(object);//序列化后的xml报文
        ByteBuf byteBuf = Unpooled.copiedBuffer(request.getBytes(UTF_8));
        return byteBuf;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context,Throwable e) throws Exception{
        logger.error(String.format("编码器抛出异常【%s】",e));
    }
}
