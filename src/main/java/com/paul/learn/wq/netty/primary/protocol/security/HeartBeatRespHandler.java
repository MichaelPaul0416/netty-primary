package com.paul.learn.wq.netty.primary.protocol.security;

import com.paul.learn.wq.netty.primary.protocol.header.MessageType;
import com.paul.learn.wq.netty.primary.protocol.header.NettyHeader;
import com.paul.learn.wq.netty.primary.protocol.header.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/16
 */
public class HeartBeatRespHandler extends ChannelHandlerAdapter{

    Logger logger = Logger.getLogger(HeartBeatRespHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext context,Object msg) throws Exception{
        NettyMessage nettyMessage = (NettyMessage) msg;
        if(nettyMessage.getHeader() != null && nettyMessage.getHeader().getType() == MessageType.HEART_REQUEST){
            logger.info("服务端收到客户端的心跳ping【"+nettyMessage+"】");
            NettyMessage response = builderNettyMessage();
            logger.info("服务端已经生成了pong心跳，准备响应给客户端【"+response+"】");
            context.writeAndFlush(response);
        }else{
            context.fireChannelRead(msg);
        }
    }

    private NettyMessage builderNettyMessage(){
        NettyMessage  nettyMessage = new NettyMessage();
        NettyHeader nettyHeader = new NettyHeader();
        nettyHeader.setType(MessageType.HEART_RESPONSE);
        nettyMessage.setHeader(nettyHeader);

        return nettyMessage;
    }
}
