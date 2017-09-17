package com.paul.learn.wq.netty.primary.http.xml.protocol.code;

import com.paul.learn.wq.netty.primary.http.xml.j2butil.XmlBeanUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;


/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/9
 */
public abstract class AbstractHttpXmlDecoder<T> extends MessageToMessageDecoder<T>{
    private Logger logger = Logger.getLogger(AbstractHttpXmlDecoder.class);

    private Class<?> clazz;

    private boolean isPrint;

    private final static Charset UTF_8 = Charset.forName("UTF-8");

    protected  AbstractHttpXmlDecoder(Class<?> clazz){
        this(clazz,true);
    }

    protected AbstractHttpXmlDecoder(Class<?> clazz,boolean isPrint){
        this.clazz = clazz;
        this.isPrint = isPrint;
    }


    protected  Object decode0(ChannelHandlerContext context, ByteBuf byteBuf) throws Exception{
        String response = byteBuf.toString(UTF_8);
        logger.info(String.format("解码器反序列化，转换为对象【%s】",clazz.getName()));
        if(isPrint){
            logger.info(String.format("打印日志信息：输出String【%s】",response));
        }
        Object object = XmlBeanUtil.convertXml2Bean(response,clazz);
        logger.info(String.format("反序列化后的对象【%s】",object));
        return object;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context,Throwable e) throws Exception{
        logger.error(String.format("解码器抛出异常【%s】",e));
    }
}
