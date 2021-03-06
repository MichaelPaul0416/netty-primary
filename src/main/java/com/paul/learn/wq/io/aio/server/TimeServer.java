package com.paul.learn.wq.io.aio.server;

import com.paul.learn.wq.io.base.AbstractObject;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/21
 */
public class TimeServer extends AbstractObject {

    public static void main(String args[]){
        int port = checkPort(args,8080);
        Thread server = new Thread(new ASynTimeServerHandler(port));
        server.start();
    }
}
