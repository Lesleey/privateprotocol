package com.netty.privateprotocol.messagemodel;

import java.io.Serializable;

/**
 * @author Lesleey
 * @date 2021/7/1-21:43
 * @desc  私有协议的完整消息由两部分组成
 */
public class RequestMessage implements Serializable {

    /**
     *  请求头
     */
    private RequestHeader header;

    /**
     *  请求体
     */
    private Object body;


    public RequestHeader getHeader() {
        return header;
    }

    public void setHeader(RequestHeader header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "RequestMessage{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }
}
