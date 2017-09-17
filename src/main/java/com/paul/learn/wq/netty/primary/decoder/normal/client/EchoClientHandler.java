package com.paul.learn.wq.netty.primary.decoder.normal.client;

import com.paul.learn.wq.netty.primary.decoder.normal.bean.Person;
import com.paul.learn.wq.netty.primary.decoder.normal.util.ReflectionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import org.msgpack.type.ArrayValue;

import java.lang.reflect.InvocationTargetException;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/27
 */
public class EchoClientHandler extends ChannelHandlerAdapter {
    private static final String DELIMITER = "$_";

    private String request = "netty framework--hello world--";

    private Logger logger = Logger.getLogger(EchoClientHandler.class);

    private int order;

    @Override
    public void channelActive(ChannelHandlerContext context){
//        txtDecoder(context);
        for(int i=0;i<2;i++){
            Person person = new Person();
            person.setName("Paul[" + i +"]");
            person.setAge((int)(Math.random()*100));
            person.setPhone("158"+ (long)(Math.random()*100000000));
            context.write(person);
        }
        context.flush();
    }



    private void txtDecoder(ChannelHandlerContext context) {
        logger.info("netty echo client will message 10 times");
        ByteBuf buffer ;
        for(int i=0;i<10;i++) {
            buffer = Unpooled.copiedBuffer((request + System.currentTimeMillis() +"." + DELIMITER).getBytes());
            context.writeAndFlush(buffer);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext context,Object msg){
//        txtRead((String) msg);
        ArrayValue arrayValue = (ArrayValue) msg;

        try {
            String name = ReflectionUtil.builderInstance(arrayValue,Person.class).getName();
            logger.info("receive response from bean server --> "+ name.replaceAll("\"|\\\\",""));

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void txtRead(String msg) {
        String result = msg;
        logger.info("times["+ ++order +"] receive message from server --> " + result);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) throws Exception{
        context.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context,Throwable e){
        e.printStackTrace();
        context.close();
        logger.error("error message --> " + e.getMessage());
    }
}
