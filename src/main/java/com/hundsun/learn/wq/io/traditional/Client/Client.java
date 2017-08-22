package com.hundsun.learn.wq.io.traditional.Client;

import com.hundsun.learn.wq.io.base.AbstractObject;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/6
 */
public class Client extends AbstractObject{
    private static Logger logger = Logger.getLogger(Client.class);

    public static void main(String args[]){
        BufferedReader in = null;
        PrintWriter out = null;
        Socket socket = null;
        try {
            int port = checkPort(args,8080);
            socket = new Socket("127.0.0.1",port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           /* out = new PrintWriter(socket.getOutputStream());
            out.println("QUERY_CURRENT_TIME");
            out.flush();*/
//            或者采用下面的方法
            out = new PrintWriter(socket.getOutputStream(),true);
            out.println("QUERY_CURRENT_TIME");
            out.flush();

            logger.info("send time_query command succeed");
            String response = in.readLine();
            logger.info("the time server response message : "+response);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            out.close();
            out = null;
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            in = null;
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
    }
}
