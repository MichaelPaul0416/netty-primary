package com.hundsun.learn.wq.netty.nio.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/7
 */
public class MultiThreadTimeServer implements Runnable {

    private Logger logger = Logger.getLogger(MultiThreadTimeServer.class);

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    private volatile boolean stop;

    public MultiThreadTimeServer(int port){
        try {
            logger.info("time server starts initialization...");
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);//设置非阻塞
            serverSocketChannel.socket().bind(new InetSocketAddress("127.0.0.1",port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);//绑定到多路复用器上，监听接受请求的操作
            logger.info("time server ends initialization...bind ip --> 127.0.0.1 and port --> " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public void stop(){
        this.stop = true;
    }

    @Override
    public void run() {
        logger.info("Scanning thread starts scan...");
        while(!stop){
            try {
                selector.select(1000);//1000ms轮询一次
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey selectionKey = null;
                while(iterator.hasNext()){
                    selectionKey = iterator.next();
                    iterator.remove();
                    try{
                        handleInput(selectionKey);
                    }catch (Exception e){
                        if(selectionKey != null){
                            logger.info("stop selectionKey ["+selectionKey.toString()+"]");
                            selectionKey.cancel();
                            if(selectionKey.channel() != null){
                                logger.info("close the channel ["+selectionKey.channel().toString()+"] of this selectionKey");
                                selectionKey.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if(selector != null){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey selectionKey) throws IOException {
        if(selectionKey.isValid()){
            if(selectionKey.isAcceptable()){
                logger.info("start obtain client channel and regist it to selector...");
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                socketChannel.register(selector,SelectionKey.OP_READ);
                logger.info("client channel ["+socketChannel+"] has been registed to selector...");
            }
            if(selectionKey.isReadable()){
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int readBytes = socketChannel.read(byteBuffer);
                logger.info("start read the bytes of this channel ["+selectionKey.channel()+"]");
                logger.info("allocate the space of this channel...");
                if(readBytes > 0){
                    byteBuffer.flip();
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
                    String request = new String(bytes,"UTF-8");
                    logger.info("time server receives this order --> "+request);
                    String response ;
                    if("QUERY_CURRENT_TIME".equalsIgnoreCase(request)){
                        logger.info("time server is ready to answer for this request");
                        response = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    }else{
                        response = "BAD REQUEST FOR TIME SERVER";
                    }
                    logger.info("response message to client by this channel ["+socketChannel+"]");
                    doWrite(socketChannel,response);
                    logger.info("the message ["+response+"] response to client successfully...");
                }else if(readBytes < 0){
                    selectionKey.cancel();
                    socketChannel.close();
                }
            }
        }
    }

    private void doWrite(SocketChannel socketChannel,String response) throws IOException {
        if(response != null && response.trim().length() > 0){
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            socketChannel.write(writeBuffer);
        }
    }
}
