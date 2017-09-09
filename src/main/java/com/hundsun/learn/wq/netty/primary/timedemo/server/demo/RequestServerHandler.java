package com.hundsun.learn.wq.netty.primary.timedemo.server.demo;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RequestServerHandler extends ChannelHandlerAdapter{
    private Logger logger = Logger.getLogger(RequestServerHandler.class);

    private SimpleDateFormat simpleDateFormat;

    {
        simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ssss");
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        logger.info("--------------------------------handler channelActive--------------------------------");

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception{


        SubscribeResp resp = new SubscribeResp();
        SubscribeReq req = (SubscribeReq)msg;   // 订购内容


        resp.setnSubReqID(req.getSubReqID());
        resp.setRespCode(0);
        resp.setDesc("-------response message["+simpleDateFormat.format(new Date())+"]-------");
        ctx.writeAndFlush(resp);    // 反馈消息


        if("XXYY".equalsIgnoreCase(req.getUserName())){
            logger.info("接收到的数据: [  " + req.toString() + "  ]");
        }

    }

    @Override
    public  void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        logger.info("---------------exceptionCaught 网络异常，关闭网络---------------");
        cause.printStackTrace();
        ctx.close();
    }
}