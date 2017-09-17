package com.paul.learn.wq.netty.primary.decoder.normal.server;

import com.paul.learn.wq.netty.primary.decoder.normal.code.MsgBeanDecoder;
import com.paul.learn.wq.netty.primary.decoder.normal.code.MsgBeanEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.log4j.Logger;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/27
 */
public class DecoderEchoServer {
    private Logger logger = Logger.getLogger(DecoderEchoServer.class);

    public void bind(int port)  {

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss,worker);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG,100);
        bootstrap.handler(new LoggingHandler(LogLevel.INFO));
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {

                /**
                 * 这个构造方法中，第一个参数表示单条消息的最大长度，如果达到该长度后还是没有找到分隔符，就抛出TooLongFrameException，防止异常码流确实分隔符导致的内存溢出
                 * 第二个就是分隔符对象
                 * 注释掉下面这一行的话就是不做粘包拆包处理，所以就会如果长度没有溢出，则会一次性收到所有的报文，在这种情况下，服务端就之所了一次处理
                 */
//                fixedLength(socketChannel);
//                delimiter(socketChannel);

                /**
                 * 自定义编码解码器
                 */
                socketChannel.pipeline().addLast("frameEncoder",new LengthFieldPrepender(2));
                socketChannel.pipeline().addLast("BeanEncoder",new MsgBeanEncoder());
                socketChannel.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(65535,0,2,0,2));
                socketChannel.pipeline().addLast("BeanDecoder",new MsgBeanDecoder());
                socketChannel.pipeline().addLast(new EchoServerHandler());
            }

            private void fixedLength(SocketChannel socketChannel) {
                socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(20));
                txtDecoderCommon(socketChannel);
            }

            private void delimiter(SocketChannel socketChannel) {
                ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes()) ;
                socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
                txtDecoderCommon(socketChannel);
            }

            private void txtDecoderCommon(SocketChannel socketChannel) {
                socketChannel.pipeline().addLast(new StringDecoder());
                socketChannel.pipeline().addLast(new EchoServerHandler());
            }
        });

        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        }catch (InterruptedException e){
            logger.error("errorMsg:"+e.getMessage());
        }finally {
            boss.shutdownGracefully() ;
            worker.shutdownGracefully();
        }

    }

    public static  void main(String args[]){
        DecoderEchoServer server = new DecoderEchoServer();
        server.bind(8080);
    }
}
