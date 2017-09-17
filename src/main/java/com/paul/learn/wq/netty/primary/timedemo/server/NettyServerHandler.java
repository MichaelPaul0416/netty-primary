package com.paul.learn.wq.netty.primary.timedemo.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/22
 */
public class NettyServerHandler extends ChannelHandlerAdapter{

    private Logger logger = Logger.getLogger(NettyServerHandler.class);

    private  int order;

    @Override
    public void channelActive(ChannelHandlerContext context){
        logger.info("--------------------------------handler channelActive--------------------------------");
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info(String.format("ready to construct request from client --> %s",ctx.channel().remoteAddress()));
        String request = (String) msg;
        System.out.println("netty time server receive order : " + request + " and the order is : " + ++order);
        String response;
        if("QUERY_CURRENT_TIME".equals(request)){
            logger.info(String.format("netty time server has been received the correct request..."));
            response = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + System.getProperty("line.separator");


        }else{
            response = "wrong request,please input [QUERY_CURRENT_TIME]" + System.getProperty("line.separator");
        }
        ByteBuf respByte = Unpooled.copiedBuffer(response.getBytes());
        ctx.writeAndFlush(respByte);
        logger.info(String.format("netty time server has written the response message to the channel..."));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        logger.info(String.format("all the response has been written and these will be response to client..."));
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(String.format("something wrong has been occurred and the message is --> %s",cause.getMessage()));
        ctx.close();
    }

}
