package com.paul.learn.wq.netty.primary.protocol.security;

import com.paul.learn.wq.netty.primary.protocol.header.MessageType;
import com.paul.learn.wq.netty.primary.protocol.header.NettyHeader;
import com.paul.learn.wq.netty.primary.protocol.header.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/16
 */
public class HeartBeatReqHandler extends ChannelHandlerAdapter{

    Logger logger = Logger.getLogger(HeartBeatReqHandler.class);

    private volatile ScheduledFuture<?> heartBeat;

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception{
        logger.info("收到服务器的心跳响应pong");
        NettyMessage nettyMessage = (NettyMessage) msg;
        //登录响应
        if(nettyMessage.getHeader() != null && nettyMessage.getHeader().getType() == MessageType.HANDS_RESPONSE){
            heartBeat = context.executor().scheduleAtFixedRate(new HeartBeatReqHandler.HearBeatTask(context),0,5000, TimeUnit.MILLISECONDS);
        }else if(nettyMessage.getHeader() != null && nettyMessage.getHeader().getType() == MessageType.HEART_RESPONSE){//心跳响应
            logger.info("收到服务器的相应心跳信息【"+nettyMessage+"】");
        }else{
            context.fireChannelRead(nettyMessage);
        }
    }

    private class HearBeatTask implements Runnable{
        private final ChannelHandlerContext channelHandlerContext;

        public HearBeatTask(final ChannelHandlerContext context){
            this.channelHandlerContext = context;
        }



        @Override
        public void run() {
            NettyMessage nettyMessage = buildHeartBeat();
            logger.info("客户端心跳任务准备好心跳bean【"+nettyMessage+"】,准备发送");
            this.channelHandlerContext.writeAndFlush(nettyMessage);
        }
    }

    private NettyMessage buildHeartBeat(){
        NettyMessage nettyMessage = new NettyMessage();
        NettyHeader nettyHeader = new NettyHeader();
        nettyHeader.setType(MessageType.HEART_REQUEST);
        nettyMessage.setHeader(nettyHeader);
        return nettyMessage;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context,Throwable e) throws Exception{
        if(heartBeat != null){
            heartBeat.cancel(true);
            heartBeat = null;
        }

        context.fireExceptionCaught(e);
    }
}
