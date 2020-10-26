package com.czz.nio.netty.client;

import com.czz.nio.netty.client.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @author czz
 * @version 1.0
 * @date 2020/10/26 21:30
 */
public class Client {
    private static Logger logger =Logger.getLogger("Client");
    private InetSocketAddress address;
    public Client(InetSocketAddress address) {
        this.address = address;
    }
    public void init() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("Encoder", new StringEncoder());
                            pipeline.addLast("Decoder", new StringDecoder());
                            pipeline.addLast(new ClientHandler());
                        }
                    });
            ChannelFuture cf = b.connect(address).sync();
            logger.info("客户端"+cf.channel().localAddress()+"上线");

            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()){
                String s = scanner.nextLine();
                cf.channel().writeAndFlush(s);
            }
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
