package com.paul.learn.wq.netty.primary.protocol.code;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.*;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/16
 */
public class MarshallingDecoder {

    private Unmarshaller unmarshaller;



    public MarshallingDecoder() throws Exception {
        MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        unmarshaller = marshallerFactory.createUnmarshaller(configuration);
    }

    protected Object decode(ByteBuf byteBuf) throws Exception{
        try {
            int objectSize = byteBuf.readInt();
            ByteBuf readBuf = byteBuf.slice(byteBuf.readerIndex(),objectSize);
            ByteInput input = new ChannelBufferByteInput(readBuf);
            unmarshaller.start(input);
            Object object = unmarshaller.readObject();
            unmarshaller.finish();
            byteBuf.readerIndex(byteBuf.readerIndex() + objectSize);
            return object;
        }finally {
            unmarshaller.close();
        }
    }
}
