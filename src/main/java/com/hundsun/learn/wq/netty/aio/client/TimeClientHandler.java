package com.hundsun.learn.wq.netty.aio.client;

import org.apache.log4j.Logger;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/21
 */
public class TimeClientHandler implements Runnable {

    private int port;

    private Logger logger = Logger.getLogger(TimeClientHandler.class);

    AsynchronousSocketChannel clientChannel;

    CountDownLatch countDownLatch;
    public TimeClientHandler(int port){
        this.port = port;
        try {
            clientChannel = AsynchronousSocketChannel.open();
            logger.info("client has been initialized...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        countDownLatch = new CountDownLatch(1);

        /**
         * connect签名
         * public abstract <A> void connect(SocketAddress remote,A attachment,CompletionHandler<Void,? super A> handler);
         */
        clientChannel.connect(new InetSocketAddress("127.0.0.1",port),this,new SendReceiveCompletionHandler());

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
