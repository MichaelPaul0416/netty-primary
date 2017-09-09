package com.hundsun.learn.wq.netty.primary.decoder.protobuf.server;

import com.hundsun.learn.wq.netty.primary.decoder.protobuf.bean.SubscribeReqProto;
import com.hundsun.learn.wq.netty.primary.decoder.protobuf.bean.SubscribeRespProto;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;


/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/30
 */
public class ProtobufServerHandler extends ChannelHandlerAdapter {

    private Logger logger = Logger.getLogger(ProtobufServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        logger.info("some client has been connected to PROTOBUF SERVER --> ["+context.channel().remoteAddress()+"]");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        SubscribeReqProto.SubscribeReq subscribeReq = (SubscribeReqProto.SubscribeReq) msg;
        logger.info("--------------------------"+subscribeReq.getSubReqID()+"--------------------------");
        logger.info("receive bean request --> " + subscribeReq.toString());
        logger.info("ready to response bean for this client request");
        SubscribeRespProto.SubscribeResp response = createSubscribeResp(subscribeReq);
        ctx.write(response);
//        ctx.fireChannelRead(msg);
    }

    private SubscribeRespProto.SubscribeResp createSubscribeResp(SubscribeReqProto.SubscribeReq subscribeReq){
//        SubscribeRespProto.SubscribeResp subscribeResp
        SubscribeRespProto.SubscribeResp.Builder builder = SubscribeRespProto.SubscribeResp.newBuilder();

        builder.setSubReqID(subscribeReq.getSubReqID());
        builder.setRespCode(0);
        builder.setDesc("good book");


        return builder.build();

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        logger.info("all request in channel ["+ctx.channel().remoteAddress()+"] this time has been written");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
        cause.printStackTrace();
        ctx.close();
    }
}
