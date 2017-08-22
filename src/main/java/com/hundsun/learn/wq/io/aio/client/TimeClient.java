package com.hundsun.learn.wq.io.aio.client;

import com.hundsun.learn.wq.io.base.AbstractObject;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/21
 */
public class TimeClient extends AbstractObject {

    public static void main(String args[]){
        int port = checkPort(args,8080);
        Thread client = new Thread(new TimeClientHandler(port));
        client.start();
    }
}
