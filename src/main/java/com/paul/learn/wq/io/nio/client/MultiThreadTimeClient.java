package com.paul.learn.wq.io.nio.client;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/8
 */
public class MultiThreadTimeClient implements Runnable {
    private Logger logger = Logger.getLogger(MultiThreadTimeClient.class);

    private Selector selector;
    private SocketChannel socketChannel;
    private String host;
    private int port;
    private volatile boolean stop = false;

    public MultiThreadTimeClient(String host,int port){
        this.host = host == null ? "127.0.0.1" : host;
        this.port = port;

        try {
            socketChannel = SocketChannel.open();
            selector = Selector.open();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try{
            //connect
            doConnect();
        }catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        while (!stop){
            try {
                selector.select(1000);
                Set<SelectionKey> keySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keySet.iterator();
                SelectionKey selectionKey;
                while(iterator.hasNext()){
                    selectionKey = iterator.next();
                    iterator.remove();
                    try{
                        //handleInput
                        handleInput(selectionKey);
                    }catch(Exception e){
                        if(selectionKey != null) {
                            selectionKey.cancel();
                            if (selectionKey.channel() != null) {
                                selectionKey.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
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
            SocketChannel channel = (SocketChannel) selectionKey.channel();
            if(selectionKey.isConnectable()){
                if(channel.finishConnect()){
                    channel.register(selector,SelectionKey.OP_READ);
                    //doWrite
                    doWrite(channel);
                }else {
                    System.exit(1);
                }
            }
            if(selectionKey.isReadable()){
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int bufferLength = channel.read(buffer);
                if(bufferLength > 0){
                    buffer.flip();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    String response = new String(bytes,"UTF-8");
                    logger.info("time client received response from server --> " + response);
                    this.stop = true;
                }else if(bufferLength < 0){
                    selectionKey.cancel();
                    channel.close();
                }else {
                    ;
                }
            }

        }
    }

    private void doConnect() throws IOException {
        if(socketChannel.connect(new InetSocketAddress(host,port))){
            //连接上的话，则注册,读应答
            socketChannel.register(selector,SelectionKey.OP_READ);
            //doWrite
            doWrite(socketChannel);
        }else{//未连接的话，注册到多路复用器上，监听连接操作
            socketChannel.register(selector,SelectionKey.OP_CONNECT);
        }
    }

    private void doWrite(SocketChannel channel) throws IOException {
        byte[] requestByte = "QUERY_CURRENT_TIME".getBytes();
        ByteBuffer requestBuffer = ByteBuffer.allocate(requestByte.length);
        requestBuffer.put(requestByte);
        requestBuffer.flip();
        channel.write(requestBuffer);
        if(!requestBuffer.hasRemaining()){
            logger.info("send order 2 server succeed");
        }
    }
}
