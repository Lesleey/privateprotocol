package com.netty.privateprotocol.messagemodel;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Lesleey
 * @date 2021/7/1-21:39
 * @desc 私有协议自定义的请求头
 */
public class RequestHeader implements Serializable {


    /**
     *  校验码 OxABEF + 主版本号 + 次版本号
     */
    private int crcCode;

    /**
     *  消息头和消息体的总长度
     */
    private int length;

    /**
     *  节点的唯一标示
     */
    private long sessionID;

    /**
     *  请求类型
     *      0: 业务消息
     *      1：业务响应
     *      2. 业务 ONE WAY 消息（即是请求也是响应）
     *      3. 握手消息
     *      4. 握手应答消息
     *      5. 心跳请求消息
     *      6. 心跳应答消息
     */
    private byte type;

    /**
     *   消息优先级
     */
    private byte priority;

    /**
     *  附件长度
     */
    private Map<String, Object> attachment;

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

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
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

    @Override
    public String toString() {
        return "RequestHeader{" +
                "crcCode=" + crcCode +
                ", length=" + length +
                ", sessionID=" + sessionID +
                ", type=" + type +
                ", priority=" + priority +
                ", attachment=" + attachment +
                '}';
    }



}
