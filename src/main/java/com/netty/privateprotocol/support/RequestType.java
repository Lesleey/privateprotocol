package com.netty.privateprotocol.support;

/**
 * @author Lesleey
 * @date 2021/7/1-21:47
 * @desc 定义了私有协议的 7 种消息类型
 */
public enum RequestType {
    BUSINESS( "业务消息", (byte)0),
    BUSINESS_RESPOND("业务消息响应", (byte)1),
    ONE_WAY("业务 one way消息", (byte)2),
    HANDSNAKE("握手消息", (byte)3),
    HANDSNAKE_RESPOND("握手响应", (byte)4),
    HEARTBEAT("心跳消息", (byte)5),
    HEARTBEAT_RESPOND("心跳应答消息", (byte)6);
    private String desc;
    private byte type;

    private RequestType(String desc, byte type){
        this.desc = desc;
        this.type = type;
    }
    public byte type(){
        return this.type;
    }
}
