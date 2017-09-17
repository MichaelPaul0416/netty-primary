package com.paul.learn.wq.netty.primary.decoder.normal.bean;

import org.msgpack.annotation.Message;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/27
 */
@Message
public class Person {
    private int age;
    private String name;
    private String phone;

    @Override
    public String toString() {
        return "Person{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
