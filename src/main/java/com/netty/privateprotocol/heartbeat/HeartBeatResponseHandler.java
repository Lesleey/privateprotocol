package com.netty.privateprotocol.heartbeat;

import com.netty.privateprotocol.messagemodel.RequestHeader;
import com.netty.privateprotocol.messagemodel.RequestMessage;
import com.netty.privateprotocol.support.RequestType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author Lesleey
 * @date 2021/7/1-22:43
 * @function  用于服务端处理心跳请求, 发送心跳响应
 */
public class HeartBeatResponseHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RequestMessage reqMsg = (RequestMessage) msg;
        RequestHeader header = reqMsg.getHeader();
        // 如果收到心跳检测请求，则发出心跳响应
        if(header != null && header.getType() == RequestType.HEARTBEAT.type()){
            RequestMessage respMsg = new RequestMessage();
            RequestHeader respHeader = new RequestHeader();
            respHeader.setType(RequestType.HEARTBEAT_RESPOND.type());
            respMsg.setHeader(respHeader);
            ctx.writeAndFlush(respMsg);
        // 否则交由下个处理器处理
        }else
            super.channelRead(ctx, msg);
    }
}
