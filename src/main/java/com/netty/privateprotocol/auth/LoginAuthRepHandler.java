package com.netty.privateprotocol.auth;

import com.netty.privateprotocol.messagemodel.RequestHeader;
import com.netty.privateprotocol.messagemodel.RequestMessage;
import com.netty.privateprotocol.support.RequestType;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Lesleey
 * @date 2021/7/1-22:17
 * @function 服务端用来处理握手的请求, 进行消息的接入和安全认证
 */
public class LoginAuthRepHandler extends ChannelHandlerAdapter {

    private final List<String> whiteHosts;

    private final Set<String> loginOk;

    public LoginAuthRepHandler(List<String> whiteHosts){
        this.whiteHosts = whiteHosts;
        loginOk = new HashSet<>();
    }

    public LoginAuthRepHandler(){
        this(new ArrayList<>());
        whiteHosts.add("localhost");
        whiteHosts.add("192.168.3.100");
    }

    /**
     *  增加机器白名单
     * @param host
     */
    public void addWhiteHost(String host){
        whiteHosts.add(host);
    }


    /**
     *  重复登录或者白名单不包含请求的机器则拒绝链接
     *  重复登录保护: 握手成功之后, 链路处于正常的状态下, 不允许客户端重复登录, 防止客户端在异常状态下反复重连导致句柄耗尽。
     */
    private boolean isPerm(String reqHost){
        return true;
    }


    /**
     * 如果消息类型为握手请求, 则进行消息的接入和安全认证
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RequestMessage reqMsg = (RequestMessage) msg;
        RequestHeader header = reqMsg.getHeader();
        //1. 如果消息类型是握手请求, 则进行处理
        if(header != null && header.getType() == RequestType.HANDSNAKE.type()){
            System.out.println("server recive handsnake from client");

            String reqHost = ctx.channel().remoteAddress().toString();

            boolean isPermLogin = false;

            RequestMessage response = buildLoginRep();

            if(isPerm(reqHost)) {
                response.setBody(0);
                isPermLogin = true;
            }else
                response.setBody(-1);
            ctx.writeAndFlush(response);
            if(isPermLogin)
                loginOk.add(reqHost);
        //2. 否则交给一下个处理器处理
        }else
            ctx.fireChannelRead(msg);
    }

    /**
     *  定义握手响应格式
     *      1. 附件为空
     *      2. 消息类型为 4
     *      3. 消息体的值 0 表示认证成功, 否则认证失败
     */
    private RequestMessage buildLoginRep(){
        RequestMessage response = new RequestMessage();
        RequestHeader respHeader = new RequestHeader();
        response.setHeader(respHeader);
        respHeader.setType(RequestType.HANDSNAKE_RESPOND.type());
        return response;
    }

    /**
     *  当发生异常时, 将客户从登录注册表中移除, 保证后续的重连可以成功
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        loginOk.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }

}
