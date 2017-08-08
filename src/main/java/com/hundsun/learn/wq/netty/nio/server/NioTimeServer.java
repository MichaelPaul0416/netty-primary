package com.hundsun.learn.wq.netty.nio.server;

import com.hundsun.learn.wq.netty.base.AbstractObject;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/8
 */
public class NioTimeServer extends AbstractObject {
    public static void main(String args[]){
        int port = checkPort(args,8080);
        MultiThreadTimeServer timeServer = new MultiThreadTimeServer(port);
        new Thread(timeServer,"Time_Server_001").start();
    }
}
