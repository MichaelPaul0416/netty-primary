package com.paul.learn.wq.netty.primary.http.simple;

import com.sun.istack.internal.logging.Logger;
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
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/3
 */
public class HttpFileServer {

    private Logger logger = Logger.getLogger(HttpFileServer.class);

    public void bind(int port,String url) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker).channel(NioServerSocketChannel.class);
            bootstrap.option(ChannelOption.SO_BACKLOG, 100);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                    socketChannel.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65535));
                    socketChannel.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                    socketChannel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                    socketChannel.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(url));
//                    socketChannel.pipeline().addLast("fileServerHandler",new HttpServerHandler(url));
                }
            });

            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            logger.info(String.format("file http server has started and bind [%s:%s]","127.0.0.1",8080));
            channelFuture.channel().closeFuture().sync();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String args[]) throws InterruptedException {
        String defaultUrl = "/src/main/java/";
        if(args.length > 1){
            defaultUrl = args[1];
        }
        new HttpFileServer().bind(8080,defaultUrl);
    }
}
