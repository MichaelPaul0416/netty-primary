package com.paul.learn.wq.netty.primary.decoder.normal.client;

import com.paul.learn.wq.netty.primary.decoder.normal.code.MsgBeanDecoder;
import com.paul.learn.wq.netty.primary.decoder.normal.code.MsgBeanEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import org.apache.log4j.Logger;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/27
 */
public class DecoderEchoClient {

    private Logger logger = Logger.getLogger(DecoderEchoClient.class);

    public void connect(String host,int port){
        EventLoopGroup worker = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(worker);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY,true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {


                /**
                 * 注释掉下一行的话，那客户端在接受服务端的信息时，就会显示分隔符（加入服务端启用了分隔符解码器，并且在返回消息的时候手动拼加了分隔符）
                 */
//                delimiter(socketChannel);

                /**
                 * bean编解码
                 */
                socketChannel.pipeline().addLast("frameEncoder",new LengthFieldPrepender(2));
                socketChannel.pipeline().addLast("BeanEncoder",new MsgBeanEncoder());
                socketChannel.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(65535,0,2,0,2));
                socketChannel.pipeline().addLast("BeanDecoder",new MsgBeanDecoder());
                socketChannel.pipeline().addLast(new EchoClientHandler());
            }

            private void delimiter(SocketChannel socketChannel) {
                ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
                socketChannel.pipeline().addLast(new StringDecoder());
                socketChannel.pipeline().addLast(new EchoClientHandler());
            }
        });

        try {
            ChannelFuture future = bootstrap.connect(host,port).sync();

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("error message --> " + e.getMessage());
        } finally {
            worker.shutdownGracefully();
        }
    }

    public static void main(String args[]){
        String host = "127.0.0.1";
        int port = 8080;
        DecoderEchoClient client = new DecoderEchoClient();
        client.connect(host,port);
    }
}
