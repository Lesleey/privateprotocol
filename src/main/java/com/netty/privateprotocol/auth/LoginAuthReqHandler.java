package com.netty.privateprotocol.auth;

import com.netty.privateprotocol.messagemodel.RequestHeader;
import com.netty.privateprotocol.messagemodel.RequestMessage;
import com.netty.privateprotocol.support.RequestType;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Lesleey
 * @date 2021/7/1-22:09
 * @function 客户端用来进行握手认证, 当通道被激活时, 发起认证(握手)请求
 */
public class LoginAuthReqHandler  extends ChannelHandlerAdapter {

    /**
     *  当通道被激活时发起登录请求
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildLoginReq());
    }

    /**
     *  定义握手请求的消息格式
     *      1. 消息头的 type 字段为 3
     *      2. 消息体和附件为空
     *      (握手消息的长度为 22 个字节)
     *
     */
    private RequestMessage buildLoginReq() {
        RequestMessage reqMsg = new RequestMessage();
        RequestHeader header = new RequestHeader();
        reqMsg.setHeader(header);
        header.setType(RequestType.HANDSNAKE.type());
        return reqMsg;
    }


    /**
     *  处理握手响应
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        RequestMessage reqMsg = (RequestMessage) msg;
        RequestHeader header = reqMsg.getHeader();
        //1. 如果读取的自定义的消息是对握手的应答, 则进行处理
        if(header != null && header.getType() == RequestType.HANDSNAKE_RESPOND.type()){

            System.out.println("client recive handsnake_respond from server");
            Integer handSnakeRespond = (Integer) reqMsg.getBody();
            //1.1 如果不同意连接，则关闭通道
            if(handSnakeRespond == null || handSnakeRespond != 0)
                ctx.channel().close();
            else
                ctx.fireChannelRead(msg);

        //2. 如果是其他消息类型, 则交由下一个处理器处理
        }else
            ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}
