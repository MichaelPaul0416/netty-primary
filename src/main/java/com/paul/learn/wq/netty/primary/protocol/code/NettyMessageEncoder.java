package com.paul.learn.wq.netty.primary.protocol.code;

import com.paul.learn.wq.netty.primary.protocol.header.NettyHeader;
import com.paul.learn.wq.netty.primary.protocol.header.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;
import java.util.Map;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/16
 */
public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {

    MarshallingEncoder marshallingEncoder;
    public NettyMessageEncoder() throws Exception {
        this.marshallingEncoder = new MarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyMessage nettyMessage, List<Object> list) throws Exception {

        if(nettyMessage == null || nettyMessage.getHeader() == null){
            throw  new Exception("netty协议对象为空或者netty协议头为空");
        }
        ByteBuf sendBuf = Unpooled.buffer();
        NettyHeader header = nettyMessage.getHeader();
        sendBuf.writeInt(header.getCrcCode());
        sendBuf.writeInt(header.getLength());
        sendBuf.writeLong(header.getSessionId());
        sendBuf.writeByte(header.getType());
        sendBuf.writeByte(header.getPriority());

        //写入附件
        sendBuf.writeInt(header.getAttachment().size());
        String key;
        byte[] keyByte;
        Object value;
        for(Map.Entry<String,Object> entry : header.getAttachment().entrySet()){
            key = entry.getKey();
            keyByte = key.getBytes("UTF-8");
            sendBuf.writeInt(keyByte.length);
            sendBuf.writeBytes(keyByte);
            value = entry.getValue();
            marshallingEncoder.encode(value,sendBuf);
        }


        if(nettyMessage.getBody() != null){
            marshallingEncoder.encode(nettyMessage.getBody(),sendBuf);
        }else {
            sendBuf.writeInt(0);

        }
        sendBuf.setInt(4,sendBuf.readableBytes()-8);
        list.add(sendBuf);
    }
}
