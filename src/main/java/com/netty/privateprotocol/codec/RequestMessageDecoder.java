package com.netty.privateprotocol.codec;

import com.netty.privateprotocol.messagemodel.RequestHeader;
import com.netty.privateprotocol.messagemodel.RequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lesleey
 * @date 2021/7/1-21:52
 * @function  请求消息解码器, 将缓冲区中的内容解码为 自定义的消息体
 */
public class RequestMessageDecoder extends LengthFieldBasedFrameDecoder {

    private static final JdkSerializer jdkSerializer = new JdkSerializer();

    public RequestMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {

        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);

    }

    /**
     *  解码规则如下:
     *    1. 对于固定长度的请求头字段, 按照编码的顺序进行读取
     *    2. 对于可变长度的请求头字段(附件), 首先获取附件的长度(int), 如果为 0 则退出, 否则循环读取附加内容
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf decodeByteBuf = (ByteBuf) super.decode(ctx, in);
        if(decodeByteBuf == null)
            return null;
        RequestMessage reqMsg = new RequestMessage();
        RequestHeader header = new RequestHeader();
        reqMsg.setHeader(header);

        header.setCrcCode(decodeByteBuf.readInt());
        header.setLength(decodeByteBuf.readInt());
        header.setSessionID(decodeByteBuf.readLong());
        header.setType(decodeByteBuf.readByte());
        header.setPriority(decodeByteBuf.readByte());

        int attachmentSize = decodeByteBuf.readInt();
        if(attachmentSize != 0){
            Map<String, Object> attachment = new HashMap<>();
            while(attachmentSize -- > 0){
                int keyLength = decodeByteBuf.readInt();
                byte[] keyByte = new byte[keyLength];
                decodeByteBuf.readBytes(keyByte);
                attachment.put(new String(keyByte, Charset.forName("UTF-8")),
                        jdkSerializer.readObject(decodeByteBuf));
            }
        }
        // 处理请求体
        reqMsg.setBody(jdkSerializer.readObject(decodeByteBuf));
        return reqMsg;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        cause.printStackTrace();
    }
}
