package com.netty.privateprotocol.heartbeat;

import com.netty.privateprotocol.messagemodel.RequestHeader;
import com.netty.privateprotocol.messagemodel.RequestMessage;
import com.netty.privateprotocol.support.RequestType;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Lesleey
 * @date 2021/7/1-22:33
 * @function  用于客户端发送心跳消息, 检测服务端和链路的可用性
 */
public class HeartBeatRequestHandler  extends ChannelHandlerAdapter {

    private volatile ScheduledFuture<?> heartBeat;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RequestMessage reqMsg = (RequestMessage) msg;
        RequestHeader header = reqMsg.getHeader();
        //1. 如果消息是握手的响应, 则表示已经认证成功, 则开始启动定时心跳任务
        if(header != null && header.getType() == RequestType.HANDSNAKE_RESPOND.type()){
            heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx), 0, 10, TimeUnit.SECONDS);
        //2. 如果消息是心跳的响应, 则自定义处理
        }else if(header != null && header.getType() == RequestType.HEARTBEAT_RESPOND.type()){
            System.out.println("client recive heartbeat from server ....");
        //3. 如果为其他格式, 则透传
        }else
            super.channelRead(ctx, msg);
    }

    /*
     * 定时心跳任务, 用于发送心跳
     */
    private class HeartBeatTask implements Runnable{

        private ChannelHandlerContext ctx;

        public HeartBeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            System.out.println("client send heartbeat to server ....");
            ctx.writeAndFlush(buildHeartBeatMsg());
        }

        /**
         *  定义心跳请求格式
         *      1. 请求类型为 5
         *      2. 附件和消息体为空
         */
        private RequestMessage buildHeartBeatMsg(){
            RequestMessage reqMsg = new RequestMessage();
            RequestHeader header = new RequestHeader();
            reqMsg.setHeader(header);
            header.setType(RequestType.HEARTBEAT.type());
            return reqMsg;
        }
    }
}
