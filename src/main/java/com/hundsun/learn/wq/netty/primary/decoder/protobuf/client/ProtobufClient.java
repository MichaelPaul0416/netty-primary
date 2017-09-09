package com.hundsun.learn.wq.netty.primary.decoder.protobuf.client;

import com.hundsun.learn.wq.netty.primary.decoder.protobuf.bean.SubscribeReqProto;
import com.hundsun.learn.wq.netty.primary.decoder.protobuf.bean.SubscribeRespProto;
import com.hundsun.learn.wq.netty.primary.decoder.protobuf.marshalling.MarshallingCodeFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.apache.log4j.Logger;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/31
 */
public class ProtobufClient {

    private Logger logger = Logger.getLogger(ProtobufClient.class);

    public void connect(String host,int port) throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.TCP_NODELAY,true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
//                    userMarshalling(socketChannel);

                    userProtobuf(socketChannel);
                    socketChannel.pipeline().addLast(new ProtobufClientHandler());
                }
                private void userMarshalling(SocketChannel socketChannel) {
                    socketChannel.pipeline().addLast(MarshallingCodeFactory.builderMarshallingDecoder());
                    socketChannel.pipeline().addLast(MarshallingCodeFactory.builderMarshallingEncoder());
                }

                private void userProtobuf(SocketChannel socketChannel) {
                    socketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                    socketChannel.pipeline().addLast(new ProtobufDecoder(SubscribeReqProto.SubscribeReq.getDefaultInstance()));
                    socketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                    socketChannel.pipeline().addLast(new ProtobufEncoder());
                }
            });

            ChannelFuture future = bootstrap.connect(host,port).sync();

            future.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }

    }

    public static void main(String args[]) throws InterruptedException {
        ProtobufClient client = new ProtobufClient();
        client.connect("127.0.0.1",8080);
    }
}
