package com.paul.learn.wq.netty.primary.http.xml.protocol.client;

import com.paul.learn.wq.netty.primary.http.xml.bean.Order;
import com.paul.learn.wq.netty.primary.http.xml.protocol.code.HttpXmlRequestEncoder;
import com.paul.learn.wq.netty.primary.http.xml.protocol.code.HttpXmlResponseDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import org.apache.log4j.Logger;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/9
 */
public class HttpXmlClient {

    private Logger logger = Logger.getLogger(HttpXmlClient.class);

    public void connect(String host,int port) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(boss);

            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast("http-decoder", new HttpResponseDecoder());
                    socketChannel.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                    socketChannel.pipeline().addLast("netty-decoder", new HttpXmlResponseDecoder(Order.class));

                    socketChannel.pipeline().addLast("http-encoder", new HttpRequestEncoder());
                    socketChannel.pipeline().addLast("netty-encoder", new HttpXmlRequestEncoder());

                    socketChannel.pipeline().addLast(new HttpClientXmlHandler());
                }
            });

            ChannelFuture future = bootstrap.connect(host, port).sync();

            future.channel().closeFuture().sync();
        }finally {
            boss.shutdownGracefully();
        }
    }

    public static void main(String args[]) throws InterruptedException {
        HttpXmlClient client = new HttpXmlClient();
        client.connect("127.0.0.1",8080);
    }
}
