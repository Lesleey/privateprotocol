package com.netty.privateprotocol.app;

import com.netty.privateprotocol.auth.LoginAuthRepHandler;
import com.netty.privateprotocol.codec.RequestMessageDecoder;
import com.netty.privateprotocol.codec.RequestMessageEncoder;
import com.netty.privateprotocol.heartbeat.HeartBeatResponseHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;


/**
 * @author Lesleey
 * @date 2021/7/1-22:46
 * @function
 */
public class Server {

    public void run(){
        EventLoopGroup boss = new NioEventLoopGroup(), work = new NioEventLoopGroup();
            ServerBootstrap server = new ServerBootstrap();
            server.group(boss, work)
                    .channel(NioServerSocketChannel .class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new RequestMessageDecoder(65535, 0, 4,0,4));
                    socketChannel.pipeline().addLast(new LengthFieldPrepender(4));
                    socketChannel.pipeline().addLast(new RequestMessageEncoder());
                    // 利用该处理器处理心跳超时，当一定周期没有读取到对方的消息，如果是服务端关闭连接，清除登录信息，等待客户端重连，
                    // 如果是客户端发起重新连接
                    socketChannel.pipeline().addLast(new ReadTimeoutHandler(50));
                    socketChannel.pipeline().addLast(new LoginAuthRepHandler());
                    socketChannel.pipeline().addLast(new HeartBeatResponseHandler());
                }
            });
            try {
                ChannelFuture sync = server.bind(new InetSocketAddress(IpHolder.SERVER_IP, IpHolder.SERVER_PORT)).sync();
                System.out.println("server is start ..........");
                sync.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                boss.shutdownGracefully();
                work.shutdownGracefully();
            }
    }

    public static void main(String[] args) {
        new Server().run();
    }
}
