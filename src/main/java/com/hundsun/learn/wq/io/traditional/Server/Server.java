package com.hundsun.learn.wq.io.traditional.Server;

import com.hundsun.learn.wq.io.base.AbstractObject;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author:wangqiang20995
 * @description:BIO服务器端
 * @Date:2017/8/6
 */
public class Server extends AbstractObject{
    private static Logger logger = Logger.getLogger(Server.class);
    public static void main(String args[]){
        int port = 8080;

        checkPort(args,port);

        ServerSocket serverSocket = null;
        try {

            serverSocket = new ServerSocket(port);
            //阻塞线程执行
            //executeByThread(serverSocket,port);
            //线程池执行
            executeInPool(serverSocket,port);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(serverSocket != null) {
                    logger.info("server will be closed...");
                    serverSocket.close();
                    serverSocket = null;
                }
            } catch (IOException e) {
                logger.error("server close failed... and the message is["+e.getMessage()+"]");
                e.printStackTrace();
            }
        }
    }


    private static void executeByThread(ServerSocket serverSocket,int port) throws IOException {
        Socket socket ;
        logger.info("server started and the port listened is ["+port+"]");
        while(true){
            socket = serverSocket.accept();
            Thread customer = new Thread(new TimeHandler(socket));
            customer.start();
        }
    }
    private static void executeInPool(ServerSocket serverSocket,int port) throws IOException {

        Socket socket;
        TimeHandlerExecutor executor = new TimeHandlerExecutor(50,10000);
        logger.info("server started and the port listened is ["+port+"]");
        while (true){
            socket = serverSocket.accept();
            executor.execute(new TimeHandler(socket));
        }
    }
}
