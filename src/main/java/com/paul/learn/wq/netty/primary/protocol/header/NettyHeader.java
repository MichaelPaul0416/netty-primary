package com.paul.learn.wq.netty.primary.protocol.header;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author:wangqiang20995
 * @description:私有协议栈中的协议头
 * @Date:2017/9/16
 */
public class NettyHeader {
    private int crcCode = 0xabef0101;

    private int length;//消息长度

    private long sessionId;//会话Id

    private byte type;//消息类型

    private byte priority;//消息优先级

    private Map<String,Object> attachment = new HashMap<>();

    @Override
    public String toString() {
        return "NettyHeader{" +
                "crcCode=" + crcCode +
                ", length=" + length +
                ", sessionId=" + sessionId +
                ", type=" + type +
                ", priority=" + priority +
                ", attachment=" + attachment +
                '}';
    }

    public int getCrcCode() {
        return crcCode;
    }

    public void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }
}
