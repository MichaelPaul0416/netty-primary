package com.paul.learn.wq.netty.primary.protocol.security;

import com.paul.learn.wq.netty.primary.protocol.header.MessageType;
import com.paul.learn.wq.netty.primary.protocol.header.NettyHeader;
import com.paul.learn.wq.netty.primary.protocol.header.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/16
 */
public class LoginAuthReqHandler extends ChannelHandlerAdapter {

    private Logger logger = Logger.getLogger(LoginAuthReqHandler.class);
    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception{
        context.writeAndFlush(builderMessage());
    }

    @Override
    public void channelRead(ChannelHandlerContext context,Object msg) throws Exception{
        NettyMessage nettyMessage = (NettyMessage) msg;
        if(nettyMessage.getHeader() != null && nettyMessage.getHeader().getType() == MessageType.HANDS_RESPONSE){
            byte loginResult = (byte) nettyMessage.getBody();
            if(loginResult != (byte)0){
                logger.info("登录失败，关闭连接");
                context.close();
            }else {
                logger.info("登录成功：" + nettyMessage);
                //传递到下一个handler中，暨HeartBeatReqHandler
                context.fireChannelRead(nettyMessage);
            }
        }else{
            context.fireChannelRead(nettyMessage);
        }
    }

    private NettyMessage builderMessage(){
        NettyMessage nettyMessage = new NettyMessage();
        NettyHeader nettyHeader = new NettyHeader();
        nettyHeader.setType(MessageType.HANDS_REQUEST);
        Map<String,Object> map = new HashMap<>();
        map.put("username","保罗");
        map.put("password","123456");
        nettyHeader.setAttachment(map);
        nettyMessage.setHeader(nettyHeader);
        return nettyMessage;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context,Throwable e) throws  Exception{
        context.fireExceptionCaught(e);
    }
}
