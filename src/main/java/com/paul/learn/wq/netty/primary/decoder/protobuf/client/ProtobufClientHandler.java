package com.paul.learn.wq.netty.primary.decoder.protobuf.client;

import com.paul.learn.wq.netty.primary.decoder.protobuf.bean.SubscribeReqProto;
import com.paul.learn.wq.netty.primary.decoder.protobuf.bean.SubscribeRespProto;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/31
 */
public class ProtobufClientHandler extends ChannelHandlerAdapter {

    private Logger logger = Logger.getLogger(ProtobufClientHandler.class);


    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        logger.info("the channel to PROTOBUF SERVER has been created --> ["+context.channel().remoteAddress()+"]");
        logger.info("ready to send bean to server...");

        SubscribeReqProto.SubscribeReq subscribeReq;
//        for(int i = 0;i<99;i++){
            subscribeReq = createSubscribeReq(1);
            context.writeAndFlush(subscribeReq);
//        }



        logger.info("bean has been serial and sent to the server");

    }

    private  SubscribeReqProto.SubscribeReq createSubscribeReq(int i){
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqID(i);
        builder.setUserName("Paul Michael");
        builder.setProductName("Netty");

        List<String> address = new ArrayList<>();
        address.add("HangZhou");
        address.add("JinHua");
        address.add("LanXi");

        builder.addAllAddress(address);

        return builder.build();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeRespProto.SubscribeResp subscribeResp = (SubscribeRespProto.SubscribeResp) msg;

        logger.info("receive response bean from server --> " + subscribeResp.toString());
    }



    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        logger.info("read flush...");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
