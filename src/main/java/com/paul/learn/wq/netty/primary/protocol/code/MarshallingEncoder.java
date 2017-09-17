package com.paul.learn.wq.netty.primary.protocol.code;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/16
 */
public class MarshallingEncoder {
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    Marshaller marshaller;

    public MarshallingEncoder() throws Exception {
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        marshaller = marshallerFactory.createMarshaller(configuration);

    }

    protected void encode(Object msg, ByteBuf byteBuf) throws Exception{
        int lengthPos = byteBuf.writerIndex();//获取写出的初始下标
        byteBuf.writeBytes(LENGTH_PLACEHOLDER);//方便之后定长解码器解码

        try {
            ChannelBufferByteOutput output = new ChannelBufferByteOutput(byteBuf);
            marshaller.start(output);
            marshaller.writeObject(msg);
            marshaller.finish();
            byteBuf.setInt(lengthPos, byteBuf.writerIndex() - lengthPos - 4);
        }finally {
            marshaller.close();
        }
    }
}
