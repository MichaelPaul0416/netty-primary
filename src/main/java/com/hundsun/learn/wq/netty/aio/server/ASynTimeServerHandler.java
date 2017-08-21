package com.hundsun.learn.wq.netty.aio.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/21
 */
public class ASynTimeServerHandler implements Runnable{

    AsynchronousServerSocketChannel socketChannel;

    private int port;

    private Logger logger = Logger.getLogger(ASynTimeServerHandler.class);

    CountDownLatch countDownLatch;

    public ASynTimeServerHandler(int port){
        try {
            socketChannel = AsynchronousServerSocketChannel.open();
            socketChannel.bind(new InetSocketAddress("127.0.0.1",port));
            logger.info(String.format("Aio TimeServer has been initialized...bind host --> %s and port --> %s","127.0.0.1",port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        countDownLatch = new CountDownLatch(1);//辅助类，在完成一组正在其他线程中执行的操作之前，允许一个或者多个线程一直等待
        doAccept();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void doAccept(){
        socketChannel.accept(this,new AcceptCompletionHandler());
    }
}
