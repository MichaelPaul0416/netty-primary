package com.paul.learn.wq.netty.primary.protocol.security;

import com.paul.learn.wq.netty.primary.protocol.header.MessageType;
import com.paul.learn.wq.netty.primary.protocol.header.NettyHeader;
import com.paul.learn.wq.netty.primary.protocol.header.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/16
 */
public class LoginAuthRespHandler extends ChannelHandlerAdapter {
    private Logger logger = Logger.getLogger(LoginAuthRespHandler.class);

    private Map<String,Boolean> nodeCheck = new ConcurrentHashMap<>();//已登录

    private String[] whiteList = new String[]{"127.0.0.1"};//白名单

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception{
        NettyMessage nettyMessage = (NettyMessage) msg;
        if(nettyMessage.getHeader() != null && nettyMessage.getHeader().getType() == MessageType.HANDS_REQUEST){
            String nodeIndex = context.channel().remoteAddress().toString();
            NettyMessage responseMessage;

            if(nodeCheck.containsKey(nodeIndex)){
                responseMessage = builderNettyMessage((byte) -1);
            }else {
                InetSocketAddress address = (InetSocketAddress) context.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOk = false;
                for (String wip : whiteList) {
                    if (wip.equals(ip)) {
                        isOk = true;
                        break;
                    }
                }

                if (isOk) {
                    nettyMessage = builderNettyMessage((byte) 0);
                    nodeCheck.put(nodeIndex, isOk);
                    logger.info("IP【" + nodeIndex + "】登陆成功");
                } else {
                    nettyMessage = builderNettyMessage((byte) -1);
                }
            }
            logger.info("netty协议响应头对象【"+nettyMessage+"】");
            context.fireChannelRead(nettyMessage);
            }else{
            context.fireChannelRead(nettyMessage);
        }

    }

    private NettyMessage builderNettyMessage(byte result){
        NettyMessage nettyMessage = new NettyMessage();
        NettyHeader header = new NettyHeader();
        header.setType(MessageType.HANDS_RESPONSE);
        nettyMessage.setHeader(header);
        nettyMessage.setBody(result);
        return  nettyMessage;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context,Throwable e) throws  Exception{
        nodeCheck.remove(context.channel().remoteAddress().toString());
        context.close();
        context.fireExceptionCaught(e);
    }
}
