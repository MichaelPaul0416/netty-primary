package com.paul.learn.wq.netty.primary.protocol.demo;

import com.paul.learn.wq.netty.primary.protocol.code.NettyMessageDecoder;
import com.paul.learn.wq.netty.primary.protocol.code.NettyMessageEncoder;
import com.paul.learn.wq.netty.primary.protocol.security.HeartBeatRespHandler;
import com.paul.learn.wq.netty.primary.protocol.security.LoginAuthRespHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/16
 */
public class NettyServer {

    public void bind(int port) throws InterruptedException {

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(boss,worker);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG,100);
        bootstrap.handler(new LoggingHandler(LogLevel.INFO));
        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                nioSocketChannel.pipeline().addLast(new NettyMessageDecoder(1024*1024,4,4));
                nioSocketChannel.pipeline().addLast(new NettyMessageEncoder());
                nioSocketChannel.pipeline().addLast("readTimeoutHandler",new ReadTimeoutHandler(500));
                nioSocketChannel.pipeline().addLast("loginAuthRespHandler",new LoginAuthRespHandler());
                nioSocketChannel.pipeline().addLast("heartBeatHandler",new HeartBeatRespHandler());
            }
        });

        ChannelFuture future = bootstrap.bind(port).sync();

        future.channel().closeFuture().sync();
    }

    public static void main(String args[]) throws InterruptedException {
        NettyServer nettyServer = new NettyServer();
        nettyServer.bind(8080);
    }


}
