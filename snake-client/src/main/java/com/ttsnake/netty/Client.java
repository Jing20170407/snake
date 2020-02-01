package com.ttsnake.netty;

import com.ttsnake.common.SnakeProto;
import com.ttsnake.netty.handler.HeartbeatClientHandle;
import com.ttsnake.view.game.GameController;
import com.ttsnake.view.login.LoginController;
import com.ttsnake.view.user.UserController;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class Client {

    @Autowired
    private LoginController loginController;

    @Autowired
    private UserController userController;

    @Autowired
    private GameController gameController;

    private NioEventLoopGroup work;
    private Channel channel;

    //
    private Label notice;


    public ChannelFuture init(String host, int port) {
        work = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class)
                .group(work)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(0,4,0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                        ch.pipeline().addLast(new ProtobufDecoder(SnakeProto.SnakeMessage.getDefaultInstance()));
                        ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                        ch.pipeline().addLast(new ProtobufEncoder());
                        ch.pipeline().addLast(new HeartbeatClientHandle());
                        ch.pipeline().addLast(loginController);
                        ch.pipeline().addLast(userController);
                        ch.pipeline().addLast(gameController);
                    }
                });
        ChannelFuture future = bootstrap.connect(host, port).addListener(f -> {
            if (f.isSuccess()) {
                log.info("connect to server " + host + ":" + port);
            } else {
                log.error("连接失败："+f.cause().toString());
            }
        });


        channel = future.channel();

        return future;
    }

    public void destroy() {
        log.info("Shutdown Netty Server...");
        if(channel != null) { channel.close();}
        work.shutdownGracefully();
        log.info("Shutdown Netty Server Success!");
    }

    public Channel getChannel() {
        return channel;
    }

    public Label getNotice() {
        return notice;
    }

    public void setNotice(Label notice) {
        this.notice = notice;
    }
}
