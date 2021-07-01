package com.netty.privateprotocol.codec;


import com.netty.privateprotocol.messagemodel.RequestHeader;
import com.netty.privateprotocol.messagemodel.RequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * @author Lesleey
 * @date 2021/7/1-22:01
 * @function  协议栈自定义消息的编码器, 用来将自定义消息编码为 byteBuf
 */
public class RequestMessageEncoder extends MessageToMessageEncoder<RequestMessage> {

    private static final JdkSerializer serializer = new JdkSerializer();

    /**
     *  编码规则如下
     *      1. 写入固定长度的请求头, 按照字段的类型将字段值写入到 bytebuf 中
     *      2. 写入可变长度的请求头(附件), 首先写入附件的数量 (int), 然后一次写入附件的每一个元素, 格式为
     *        key的长度(int) key value的长度(int) value
     *      3. 写入请求体, 格式为 请求体的长度(int) 请求体
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RequestMessage reqMsg, List<Object> list) throws Exception {
        if(reqMsg == null || reqMsg.getHeader() == null)
            throw new RuntimeException("编码的消息不合法！");
        ByteBuf byteBuf = Unpooled.buffer();

        RequestHeader header = reqMsg.getHeader();
        byteBuf.writeInt(header.getCrcCode());
        byteBuf.writeInt(header.getLength());
        byteBuf.writeLong(header.getSessionID());
        byteBuf.writeByte(header.getType());
        byteBuf.writeByte(header.getPriority());

        Map<String, Object> attachment = header.getAttachment();
        if(attachment == null || attachment.isEmpty()){
            byteBuf.writeInt(0);
        }else{
            byteBuf.writeInt(attachment.size());
            attachment.forEach((key, value) ->{
                byte[] bytes = key.getBytes(Charset.forName("UTF-8"));
                byteBuf.writeInt(bytes.length);
                byteBuf.writeBytes(bytes);
                serializer.writeObject(byteBuf, value);
            });
        }

        if(reqMsg.getBody() == null)
            byteBuf.writeInt(0);
        else
            serializer.writeObject(byteBuf, reqMsg.getBody());
        byteBuf.setInt(4, byteBuf.readableBytes());
        list.add(byteBuf);
    }
}
