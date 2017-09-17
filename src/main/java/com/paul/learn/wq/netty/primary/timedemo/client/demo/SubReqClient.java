package com.paul.learn.wq.netty.primary.timedemo.client.demo;

import com.paul.learn.wq.netty.primary.timedemo.client.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class SubReqClient {

    public void connect(int nPort, String strHost) throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            /**
             * 如果你只指定了一个EventLoopGroup，
             * 那他就会即作为一个‘boss’线程，
             * 也会作为一个‘workder’线程，
             * 尽管客户端不需要使用到‘boss’线程。
             */
            Bootstrap b = new Bootstrap();
            /**
             * 不像在使用ServerBootstrap时需要用childOption()方法，
             * 因为客户端的SocketChannel没有父channel的概念。
             */
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,  true)
                    .handler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception{
                            ch.pipeline().addLast(
                                    new ObjectDecoder(1024, ClassResolvers
                                            .cacheDisabled(this.getClass().getClassLoader())));

                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(new NettyClientHandler());
                        }
                    });

            ChannelFuture f = b.connect(strHost,  nPort).sync();


            f.channel().closeFuture().sync();

        }finally{
            System.out.println("----------------main  get channel Error !!! ---------");
          group.shutdownGracefully();
        }
    }


    public static void main(String[] args){
        int nPort = 8080;
        String strHost = "127.0.0.1";
        try {
            System.out.println("----------------main connect");
            new SubReqClient().connect(nPort, strHost);
        } catch (Exception e) {
            System.out.println("----------------main Error");
            e.printStackTrace();
        }
    }
}