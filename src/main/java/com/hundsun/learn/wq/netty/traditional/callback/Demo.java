package com.hundsun.learn.wq.netty.traditional.callback;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/20
 */
public class Demo {

    Callback callback ;



    public Demo(){}

    //回调函数，留给别人调用
    public void show(){
        System.out.println("hello world and this is callback-function");
    }


    public void hello(Callback callback){
        this.callback = callback;
        this.callback.display(this);
    }

    public static void main(String args[]){
        Demo demo = new Demo();
        Callback callback = new Callback() {
            @Override
            public void display(Demo demo) {
                System.out.println("调用被回调方的方法开始...");
                demo.show();
                System.out.println("调用被回调方的方法结束...");
            }
        };
        demo.hello(callback);
    }
}
