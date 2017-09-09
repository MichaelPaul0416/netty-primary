package com.hundsun.learn.wq.netty.primary.decoder.normal.code;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.apache.log4j.Logger;
import org.msgpack.MessagePack;

import java.util.List;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/27
 */
public class MsgBeanDecoder extends MessageToMessageDecoder<ByteBuf> {

    private Logger logger = Logger.getLogger(MsgBeanDecoder.class);
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        logger.info("start to decoder byte array to object");
        final int length = byteBuf.readableBytes();
        byte[] bytes = new byte[length];
        byteBuf.getBytes(byteBuf.readerIndex(),bytes,0,length);
        MessagePack messagePack = new MessagePack();
        list.add(messagePack.read(bytes));
        logger.info("end to decoder byte array to object");
    }
}
