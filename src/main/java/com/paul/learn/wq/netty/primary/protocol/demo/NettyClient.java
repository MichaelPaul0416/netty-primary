package com.paul.learn.wq.netty.primary.protocol.demo;

import com.paul.learn.wq.netty.primary.protocol.code.NettyMessageDecoder;
import com.paul.learn.wq.netty.primary.protocol.code.NettyMessageEncoder;
import com.paul.learn.wq.netty.primary.protocol.security.HeartBeatReqHandler;
import com.paul.learn.wq.netty.primary.protocol.security.LoginAuthReqHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/16
 */
public class NettyClient {
    Logger logger = Logger.getLogger(NettyClient.class);

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    EventLoopGroup group = new NioEventLoopGroup();

    public void connect(String host,int port) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.TCP_NODELAY,true);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                    nioSocketChannel.pipeline().addLast(new NettyMessageDecoder(1024*1024,4,4));
                    nioSocketChannel.pipeline().addLast("MessageEncoder",new NettyMessageEncoder());
                    nioSocketChannel.pipeline().addLast("readTimeoutHandler",new ReadTimeoutHandler(5000));
                    nioSocketChannel.pipeline().addLast("LoginAuthHandler",new LoginAuthReqHandler());
                    nioSocketChannel.pipeline().addLast("HeartBeatHandler",new HeartBeatReqHandler());
                }
            });

            ChannelFuture future = bootstrap.connect(new InetSocketAddress(host,port)).sync();
            logger.info("客户端启动成功");
            future.channel().closeFuture().sync();
        }finally {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        logger.info("出现意外，休息100秒...");
                        TimeUnit.SECONDS.sleep(500);
                        logger.info("开始重连...");
                        connect(host,port);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void main(String args[]) throws InterruptedException {
        NettyClient nettyClient = new NettyClient();
        nettyClient.connect("127.0.0.1",8080);
    }
}
