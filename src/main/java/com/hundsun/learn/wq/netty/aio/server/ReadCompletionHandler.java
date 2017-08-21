package com.hundsun.learn.wq.netty.aio.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/21
 */
public class ReadCompletionHandler implements CompletionHandler<Integer,ByteBuffer> {
    private Logger logger = Logger.getLogger(ReadCompletionHandler.class);

    private AsynchronousSocketChannel receiveChannel;

    public ReadCompletionHandler(AsynchronousSocketChannel receiveChannel){
        if(this.receiveChannel == null) {
            logger.info(String.format("receive request channel --> %s",receiveChannel));
            this.receiveChannel = receiveChannel;
        }
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        logger.info("start deal with InputStream...");
        attachment.flip();
        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);
        String request = new String(body, Charset.forName("utf-8"));
        logger.info(String.format("request has been received --> %s",request));
        if("QUERY_CURRENT_TIME".equals(request)){
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            if(time != null && time.length() > 0){
                logger.info(String.format("ready to write response to client..."));
                byte[] respByte = time.getBytes(Charset.forName("utf-8"));
                ByteBuffer respBuffer = ByteBuffer.allocate(respByte.length);
                respBuffer.put(respByte);
                respBuffer.flip();
                receiveChannel.write(respBuffer, respBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        if(attachment.hasRemaining()){
                            receiveChannel.write(attachment,attachment,this);
                        }
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        try {
                            logger.error(String.format("something wrong has been occurred...and will close the channel --> %s",receiveChannel.getClass().getName()));
                            receiveChannel.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                logger.info(String.format("all the response has been written..."));
            }
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            logger.error(String.format("something wrong has been occurred...and will close the channel --> %s",receiveChannel.getClass().getName()));
            receiveChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
