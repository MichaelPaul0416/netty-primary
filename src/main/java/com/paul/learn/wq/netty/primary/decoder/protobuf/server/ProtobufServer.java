package com.paul.learn.wq.netty.primary.decoder.protobuf.server;

import com.paul.learn.wq.netty.primary.decoder.protobuf.bean.SubscribeReqProto;
import com.paul.learn.wq.netty.primary.decoder.protobuf.marshalling.MarshallingCodeFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.log4j.Logger;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/30
 */
public class ProtobufServer {

    private Logger logger = Logger.getLogger(ProtobufServer.class);

    public void bind(int port) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {

            ServerBootstrap bootstrap = new ServerBootstrap();


            bootstrap.group(boss, worker);
            bootstrap.channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100);
            bootstrap.handler(new LoggingHandler(LogLevel.INFO));
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    userProtobuf(socketChannel);
//                    userMarshalling(socketChannel);
                    socketChannel.pipeline().addLast(new ProtobufServerHandler());
                }

                private void userMarshalling(SocketChannel socketChannel) {

                    /**
                     * 根据MarshallingDecoder的继承关系来看，继承了LengthFieldBasedFrameDecoder，但是还少一个处理粘包/拆包的decoder或者encoder，有待考究
                     */
                    socketChannel.pipeline().addLast(MarshallingCodeFactory.builderMarshallingDecoder());
                    socketChannel.pipeline().addLast(MarshallingCodeFactory.builderMarshallingEncoder());
                }

                private void userProtobuf(SocketChannel socketChannel) {
                    socketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder());//处理读半包的解码器
                    socketChannel.pipeline().addLast(new ProtobufDecoder(SubscribeReqProto.SubscribeReq.getDefaultInstance()));//解码
                    socketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                    socketChannel.pipeline().addLast(new ProtobufEncoder());
                }


            });

            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String args[]) throws InterruptedException {
        ProtobufServer server = new ProtobufServer();
        server.bind(8080);
    }
}
