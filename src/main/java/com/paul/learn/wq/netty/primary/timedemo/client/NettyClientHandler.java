package com.paul.learn.wq.netty.primary.timedemo.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

public class NettyClientHandler extends ChannelHandlerAdapter{
    private Logger logger = Logger.getLogger(NettyClientHandler.class);

    public NettyClientHandler(){

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        logger.info("----------------handler channelActive-----准备发送十个数据-------");
        ByteBuf buffer;
        String request = "QUERY_CURRENT_TIME" + System.getProperty("line.separator");
        byte[] bytes = request.getBytes();
        for(int i=0;i<100;i++){
            buffer = Unpooled.buffer(bytes.length);
            buffer.writeBytes(bytes);
            ctx.writeAndFlush(buffer);
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{

        String response = (String) msg;
        logger.info("--------channelRead--------服务器发来的数据为：[" + response + "]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
        logger.info("----------------handler channelReadComplete----------------");
        ctx.flush();
    }


    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        logger.info("----------------------------------handler exceptionCaught----------------------------------------");
        cause.printStackTrace();
        ctx.close();
    }

}