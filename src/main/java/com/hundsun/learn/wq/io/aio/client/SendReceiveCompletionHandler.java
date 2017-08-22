package com.hundsun.learn.wq.io.aio.client;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/21
 */
public class SendReceiveCompletionHandler implements CompletionHandler<Void,TimeClientHandler> {

    private Logger logger = Logger.getLogger(SendReceiveCompletionHandler.class);
    @Override
    public void completed(Void result, TimeClientHandler attachment) {
        logger.info("client will send time request to server...");
        byte[] reqByte = "QUERY_CURRENT_TIME".getBytes(Charset.forName("utf-8"));
        ByteBuffer byteBuffer = ByteBuffer.allocate(reqByte.length);
        byteBuffer.put(reqByte);
        byteBuffer.flip();
        /**
         * write签名
         * <A> void write(ByteBuffer src,A attachment,CompletionHandler<Integer,? super A> handler);
         */
        attachment.clientChannel.write(byteBuffer,byteBuffer,new RealActionClientHandler(attachment.clientChannel,attachment.countDownLatch));
    }

    @Override
    public void failed(Throwable exc, TimeClientHandler attachment) {
        try{
            logger.error(String.format("something wrong has been occurred when middle action and cause is %s",exc.getMessage()));
            attachment.clientChannel.close();
            attachment.countDownLatch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
