package com.hundsun.learn.wq.io.aio.server;

import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/21
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel,ASynTimeServerHandler> {
    private Logger logger = Logger.getLogger(AcceptCompletionHandler.class);

    @Override
    public void completed(AsynchronousSocketChannel result, ASynTimeServerHandler attachment) {
        attachment.socketChannel.accept(attachment,this);
        logger.info(String.format("this channel --> %s has been created and connected to server",result));
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        /**
         * read方法定义如下
         * public final <A> void read(ByteBuffer dst,A attachment,CompletionHandler<Integer,? super A> handler)
         */
        result.read(byteBuffer, byteBuffer,new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, ASynTimeServerHandler attachment) {
        logger.error(String.format("something wrong has been occurred --> %s",exc.getMessage()));
        attachment.countDownLatch.countDown();
    }
}
