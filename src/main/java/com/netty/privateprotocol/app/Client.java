package com.netty.privateprotocol.app;

import com.netty.privateprotocol.auth.LoginAuthReqHandler;
import com.netty.privateprotocol.codec.RequestMessageDecoder;
import com.netty.privateprotocol.codec.RequestMessageEncoder;
import com.netty.privateprotocol.heartbeat.HeartBeatRequestHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Lesleey
 * @date 2021/7/1-22:45
 * @function
 */
public class Client {

    private EventLoopGroup work = new NioEventLoopGroup();
    private ExecutorService executorService = Executors.newFixedThreadPool(1);
    private int index = 1;

    public Client() {
    }

    public void connect(String host, int port){
        Bootstrap client = new Bootstrap();
        client.group(work)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new RequestMessageDecoder(65535, 0, 4,0,4));
                        socketChannel.pipeline().addLast(new LengthFieldPrepender(4));
                        socketChannel.pipeline().addLast(new RequestMessageEncoder());
                        // 利用该处理器处理心跳超时，当一定周期没有读取到对方的消息，如果是服务端关闭连接，清除登录信息，等待客户端重连，
                        // 如果是客户端发起重新连接
                        socketChannel.pipeline().addLast(new ReadTimeoutHandler(50));
                        socketChannel.pipeline().addLast(new LoginAuthReqHandler());
                        socketChannel.pipeline().addLast(new HeartBeatRequestHandler());
                    }
                });
        try {
            ChannelFuture sync = client.connect(new InetSocketAddress(host, port)
                    , new InetSocketAddress(IpHolder.CLIENT_IP, IpHolder.CLIENT_PORT + index ++)).sync();
            System.out.println("client is start .......");
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            // 所有资源释放完成之后， 发起重新连接操作
            executorService.execute(() ->{
                try{
                    Thread.sleep(10000);
                    connect(IpHolder.SERVER_IP, IpHolder.SERVER_PORT);
                }catch (Exception e ){
                    e.printStackTrace();
                }
            });
        }
    }

    public static void main(String[] args) {
        new Client().connect(IpHolder.SERVER_IP, IpHolder.SERVER_PORT);
    }
}
