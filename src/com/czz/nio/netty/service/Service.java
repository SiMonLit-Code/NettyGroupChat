package com.czz.nio.netty.service;

import com.czz.nio.netty.service.handler.ServiceHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

/**
 * @author czz
 * @version 1.0
 * @date 2020/10/26 20:50
 */
public class Service {
    private static Logger logger = Logger.getLogger("Service");
    private InetSocketAddress address;

    //管理所有channel
    private ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public Service(InetSocketAddress address) {
        this.address = address;
    }

    public void init() {
        EventLoopGroup workGroup = new NioEventLoopGroup(8);
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel serverSocketChannel) throws Exception {
                            ChannelPipeline pipeline = serverSocketChannel.pipeline();
                            //解码
                            pipeline.addLast("Decoder", new StringDecoder());
                            //编码
                            pipeline.addLast("Encoder", new StringEncoder());

                            pipeline.addLast(new ServiceHandler(channelGroup));
                        }
                    });
            ChannelFuture cf = sb.bind(address).sync();
            logger.info("服务器上线...");
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
