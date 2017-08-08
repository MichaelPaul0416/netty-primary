package com.hundsun.learn.wq.netty.nio.client;

import com.hundsun.learn.wq.netty.base.AbstractObject;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/8
 */
public class NioTimeClient extends AbstractObject {
    public static void main(String args[]){
        int port = checkPort(args,8080);
        MultiThreadTimeClient client = new MultiThreadTimeClient("127.0.0.1",port);
        new Thread(client,"Time_Client_001").start();
    }
}
