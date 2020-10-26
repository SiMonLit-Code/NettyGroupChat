package com.czz.nio.netty.service.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 * @author czz
 * @version 1.0
 * @date 2020/10/26 20:50
 */
public class ServiceHandler extends SimpleChannelInboundHandler<String> {

    private static Logger logger = Logger.getLogger("ServiceHandler");

    //GlobalEventExecutor.INSTANCE 全局事件执行器（单例）必须加static不然每个handler一份
//    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    //或者构造函数传入
    private ChannelGroup channels;
    public ServiceHandler(ChannelGroup channels){
        this.channels = channels;
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
        logger.info(sdf.format(new Date())+" 客户端 "+ctx.channel().remoteAddress()+"已上线");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.stream().forEach(x -> {
            if (x.remoteAddress().equals(ctx.channel().remoteAddress())){
                ctx.channel().writeAndFlush(sdf.format(new Date())+" 您已上线");
            }else {
                x.writeAndFlush(sdf.format(new Date())+" 客户端 "+ctx.channel().remoteAddress()+"已上线");
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channels.stream().forEach(x -> {
            if (x.remoteAddress().equals(ctx.channel().remoteAddress())){
                ctx.channel().writeAndFlush(sdf.format(new Date())+" 您已离线");
            }else {
                x.writeAndFlush(sdf.format(new Date())+" 客户端 "+ctx.channel().remoteAddress()+"已离线");
            }
        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        logger.info(sdf.format(new Date())+" [客户端] "+ctx.channel().remoteAddress()+s);
        System.out.println(sdf.format(new Date())+" [客户端] "+ctx.channel().remoteAddress()+s);
        channels.stream().forEach(x -> {
            if (x.remoteAddress().equals(ctx.channel().remoteAddress())){
                ctx.channel().writeAndFlush(sdf.format(new Date())+"[自己]"+s);
            }else {
                x.writeAndFlush(sdf.format(new Date())+" [客户端] "+ctx.channel().remoteAddress()+s);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        logger.info(cause.getMessage());
    }
}
