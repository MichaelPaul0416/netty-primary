package com.hundsun.learn.wq.netty.primary.decoder.protobuf.marshalling;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/31
 */
public class MarshallingCodeFactory {

    public static MarshallingDecoder builderMarshallingDecoder(){

        final MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration config = new MarshallingConfiguration();
        config.setVersion(5);
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(factory,config);
        MarshallingDecoder decoder = new MarshallingDecoder(provider,1024);

        return decoder;
    }

    public static MarshallingEncoder builderMarshallingEncoder(){
        final MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);

        MarshallerProvider provider = new DefaultMarshallerProvider(factory,configuration);
        MarshallingEncoder encoder = new MarshallingEncoder(provider);

        return encoder;
    }
}
