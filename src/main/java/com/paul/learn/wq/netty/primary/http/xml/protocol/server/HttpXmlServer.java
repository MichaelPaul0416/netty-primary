package com.paul.learn.wq.netty.primary.http.xml.protocol.server;

import com.paul.learn.wq.netty.primary.http.xml.bean.Order;
import com.paul.learn.wq.netty.primary.http.xml.protocol.code.HttpXmlRequestDecoder;
import com.paul.learn.wq.netty.primary.http.xml.protocol.code.HttpXmlResponseEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/9
 */
public class HttpXmlServer {

    public void bind(int port) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(boss, worker).channel(NioServerSocketChannel.class);

            serverBootstrap.option(ChannelOption.SO_BACKLOG, 100);

            serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));

            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                    socketChannel.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                    socketChannel.pipeline().addLast("netty-decoder", new HttpXmlRequestDecoder(Order.class));

                    socketChannel.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                    socketChannel.pipeline().addLast("netty-encoder", new HttpXmlResponseEncoder());

                    socketChannel.pipeline().addLast(new HttpServerXmlHandler());
                }
            });

            ChannelFuture future = serverBootstrap.bind(port).sync();

            future.channel().closeFuture().sync();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String args[]) throws InterruptedException {
        HttpXmlServer server = new HttpXmlServer();
        server.bind(8080);
    }
}
