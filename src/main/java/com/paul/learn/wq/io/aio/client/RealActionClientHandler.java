package com.paul.learn.wq.io.aio.client;

import com.paul.learn.wq.io.aio.server.ReadCompletionHandler;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/21
 */
public class RealActionClientHandler implements CompletionHandler<Integer,ByteBuffer> {

    private AsynchronousSocketChannel clientChannel;

    private CountDownLatch countDownLatch;

    private Logger logger = Logger.getLogger(ReadCompletionHandler.class);

    public RealActionClientHandler(AsynchronousSocketChannel clientChannel,CountDownLatch countDownLatch){

        this.clientChannel = clientChannel;

        this.countDownLatch = countDownLatch;
    }
    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        if(attachment.hasRemaining()){
            logger.info("send request...");
            this.clientChannel.write(attachment,attachment,this);
        }else{
            ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);
            this.clientChannel.read(receiveBuffer, receiveBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    logger.info("client will receive message from client...");
                    attachment.flip();
                    byte[] bytes = new byte[attachment.remaining()];
                    attachment.get(bytes);
                    String response = new String(bytes, Charset.forName("utf-8"));
                    try {
                        logger.info(String.format("receive message from server[%s] and message is --> %s",clientChannel.getRemoteAddress(),response));
                    } catch (IOException e) {
                        try {
                            logger.error(String.format("something wrong has been occurred when obtain remote address and cause is %s",e.getMessage()));
                            clientChannel.close();
                            countDownLatch.countDown();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                    //成功之后关闭该客户端
                    countDownLatch.countDown();
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    try {
                        logger.error(String.format("something wrong has been occurred when read or send message and cause is %s",exc.getMessage()));
                        clientChannel.close();
                        countDownLatch.countDown();
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            logger.error(String.format("something wrong has been occurred when deal message and cause is %s",exc.getMessage()));
            clientChannel.close();
            countDownLatch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
