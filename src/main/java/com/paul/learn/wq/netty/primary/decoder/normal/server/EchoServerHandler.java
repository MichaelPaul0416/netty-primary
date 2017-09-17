package com.paul.learn.wq.netty.primary.decoder.normal.server;

import com.paul.learn.wq.netty.primary.decoder.normal.bean.Person;
import com.paul.learn.wq.netty.primary.decoder.normal.util.ReflectionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import org.msgpack.type.ArrayValue;

import java.lang.reflect.*;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/27
 */
public class EchoServerHandler extends ChannelHandlerAdapter {

    private Logger logger = Logger.getLogger(EchoServerHandler.class);

    private int order;

    @Override
    public void channelActive(ChannelHandlerContext context){
        logger.info("one channel connected,info["+context.channel().remoteAddress()+"]");
    }

    @Override
    public void channelRead(ChannelHandlerContext context,Object msg) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
//        dealWithDelimiter(context, (String) msg);
//        dealWithFixed("fixedDecoder --> " + msg);
        ArrayValue arrayValue = (ArrayValue) msg;
        Person person = ReflectionUtil.builderInstance(arrayValue,Person.class);
        logger.info("receive bean --> " + person);
        person.setName(person.getName() + System.currentTimeMillis());
        context.writeAndFlush(person);
    }






    /**
     * @Author:wangqiang20995
     * @Description:定长解码器
     * @Date:2017/8/27 22:11
     * @param:[message]
     **/
    private void dealWithFixed(String message) {
        logger.info(message);
    }


    /**
     * @Author:wangqiang20995
     * @Description:分隔符解码器
     * @Date:2017/8/27 22:10
     * @param:[context, msg]
     **/
    private void dealWithDelimiter(ChannelHandlerContext context, String msg) {

        String info = msg;
        logger.info("this is "+ ++order +" times request from client ["+context.channel().remoteAddress()+"] and context is --> "+info);
        String response = "hello client,your message sent to us is " + info +", thank you $_" ;
        ByteBuf byteBuf = Unpooled.copiedBuffer(response.getBytes());
        context.writeAndFlush(byteBuf);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) throws Exception{
        logger.info("all over response has been sent");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context,Throwable e){
        e.printStackTrace();
        context.close();
        logger.error("error message --> " + e.getMessage());
    }
}
