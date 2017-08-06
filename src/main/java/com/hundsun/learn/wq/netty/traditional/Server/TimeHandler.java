package com.hundsun.learn.wq.netty.traditional.Server;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/6
 */
public class TimeHandler implements Runnable {
    private Logger logger = Logger.getLogger(TimeHandler.class);
    private Socket socket;

    public TimeHandler(Socket socket){
        this.socket = socket;
    }


    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            String currentTime ;
            String body ;

            while(true){
                body = in.readLine();
                if(body == null) break;
                if("QUERY_CURRENT_TIME".equalsIgnoreCase(body)){
                    logger.info("the time server receives a correct time_query command from ip["+socket.getInetAddress()+"]");
                    currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    out.println(currentTime);
                    out.flush();
                    logger.info("the time server response message to client ["+socket.getInetAddress()+"] succeed");
                }else{
                    logger.info("the time server received a wrong time_query command from ip["+socket.getInetAddress()+"]");
                    out.println("WRONG COMMAND,PLEASE INPUT AGAIN");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out != null){
                out.close();
            }
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
            }
        }
    }
}
