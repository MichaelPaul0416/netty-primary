package com.hundsun.learn.wq.netty.primary.decoder.normal.code;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.log4j.Logger;
import org.msgpack.MessagePack;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/27
 */
public class MsgBeanEncoder extends MessageToByteEncoder<Object> {
    private Logger logger = Logger.getLogger(MsgBeanEncoder.class);
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        try {
            logger.info("start to transfer object to byte array --> " + o.getClass().getName());
            MessagePack messagePack = new MessagePack();
            byte[] bytes = messagePack.write(o);
            byteBuf.writeBytes(bytes);
            logger.info("end to transfer object to byte array --> " + o.getClass().getName());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
