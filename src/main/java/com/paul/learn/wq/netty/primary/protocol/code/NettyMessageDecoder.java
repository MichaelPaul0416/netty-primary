package com.paul.learn.wq.netty.primary.protocol.code;

import com.paul.learn.wq.netty.primary.protocol.header.NettyHeader;
import com.paul.learn.wq.netty.primary.protocol.header.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;

import io.netty.handler.codec.marshalling.UnmarshallerProvider;
import org.apache.log4j.Logger;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author:wangqiang20995
 * @description:netty私有协议栈解码器（将网络中接收到的流转换为netty私有协议中的对象【NettyMessage】）
 * @Date:2017/9/16
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder{

    private Logger logger = Logger.getLogger(NettyMessageDecoder.class);

    MarshallingDecoder marshallingDecoder;
    //当父类有多个有参构造器的时候，子类需要指定一个有参构造器
   public NettyMessageDecoder(int maxFrameLength,int lengthFieldOffset,int lengthFieldLength) throws Exception {
       super(maxFrameLength,lengthFieldOffset,lengthFieldLength);
       logger.info("调用父类构造器");
       initMarshallerDecoder();
       logger.info("初始化bean解码器完毕");
   }

    private void initMarshallerDecoder( ) throws Exception {
        marshallingDecoder = new MarshallingDecoder();
    }

    @Override
    protected  Object decode(ChannelHandlerContext context, ByteBuf byteBuf) throws Exception{
       ByteBuf buffer = (ByteBuf) super.decode(context,byteBuf);
       if(buffer == null){
           logger.info("缓冲区中没有数据，无法解码，直接返回");
           return null;
       }

       NettyMessage nettyMessage = new NettyMessage();
       NettyHeader header = new NettyHeader();
       logger.info("流解码器开始构造消息头");
       header.setCrcCode(buffer.readInt());
       header.setLength(buffer.readInt());
       header.setSessionId(buffer.readLong());
       header.setType(buffer.readByte());
       header.setPriority(buffer.readByte());

       //读取附件
        int attachmentSize = buffer.readInt();
        if(attachmentSize > 0){
            Map<String,Object> attachment = new HashMap<>();
            int keySize ;
            byte[] keyByte ;
            String key ;
            for(int i = 0;i< attachmentSize ;i++){
                keySize = buffer.readInt();
                keyByte = new byte[keySize];
                buffer.readBytes(keyByte);
                key = new String(keyByte,"UTF-8");
                attachment.put(key,marshallingDecoder.decode(buffer));//object是利用自己的javabean解码器解出来的对象
            }
            header.setAttachment(attachment);
        }

        //无论是否有附件，剩下来的字节中如果还有大于4个字节的，那就是body里面内容，4个字节是长度标识符
        if(buffer.readableBytes() > 4){
            nettyMessage.setBody(marshallingDecoder.decode(buffer));
        }

        nettyMessage.setHeader(header);
        return nettyMessage;
    }
}
