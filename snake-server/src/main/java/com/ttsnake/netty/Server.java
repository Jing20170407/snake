package com.ttsnake.netty;

import com.ttsnake.common.SnakeProto;
import com.ttsnake.handler.GameHandler;
import com.ttsnake.handler.UserHandler;
import com.ttsnake.netty.handler.HeartbeatHandler;
import com.ttsnake.netty.handler.SnakeHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class Server {

    @Autowired
    private GameHandler gameHandler;

    @Autowired
    private UserHandler userHandler;

    private NioEventLoopGroup boss;
    private NioEventLoopGroup work;
    private Channel channel;

    public ChannelFuture init(int port) {
        boss = new NioEventLoopGroup();
        work = new NioEventLoopGroup();
        ChannelFuture future = null;
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap
                .group(boss,work)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(5,0,0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                        ch.pipeline().addLast(new ProtobufDecoder(SnakeProto.SnakeMessage.getDefaultInstance()));
                        ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                        ch.pipeline().addLast(new ProtobufEncoder());
                        ch.pipeline().addLast(new HeartbeatHandler());
                        ch.pipeline().addLast(new NioEventLoopGroup(), new SnakeHandler(userHandler, gameHandler));
                    }
                });

        future = bootstrap.bind(port).addListener(f -> {
            if (f.isSuccess()) {
                log.info("server listen to "+port);
            }else {
                log.error(f.cause().toString());
            }
        });

        channel = future.channel();

        return future;
    }


    public void destroy() {
        log.info("Shutdown Netty Server...");
        if(channel != null) { channel.close();}
        work.shutdownGracefully();
        boss.shutdownGracefully();
        log.info("Shutdown Netty Server Success!");
    }
}
